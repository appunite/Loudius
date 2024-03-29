/*
 * Copyright 2023 AppUnite S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appunite.loudius.ui.reviewers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.analytics.EventTracker
import com.appunite.loudius.analytics.events.ReviewersEvents
import com.appunite.loudius.common.Screen.Reviewers.getInitialValues
import com.appunite.loudius.common.flatMap
import com.appunite.loudius.domain.repository.PullRequestRepository
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.FAILURE
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.SUCCESS
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.minus

sealed class ReviewersAction {
    data class Notify(val userLogin: String) : ReviewersAction()
    object OnSnackbarDismiss : ReviewersAction()
    object OnTryAgain : ReviewersAction()
}

data class ReviewersState(
    val data: Data = Data.Loading,
    val pullRequestNumber: String = "",
    val snackbarTypeShown: ReviewersSnackbarType? = null
)

sealed class Data {
    object Loading : Data()
    object Error : Data()
    data class Success(val reviewers: List<Reviewer> = emptyList()) : Data()
}

enum class ReviewersSnackbarType {
    SUCCESS, FAILURE
}

class ReviewersViewModel(
    private val repository: PullRequestRepository,
    savedStateHandle: SavedStateHandle,
    private val eventTracker: EventTracker
) : ViewModel() {
    private val initialValues = getInitialValues(savedStateHandle)

    var state: ReviewersState by mutableStateOf(ReviewersState())
        private set

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        state = state.copy(pullRequestNumber = initialValues.pullRequestNumber)
        fetchData()
    }

    fun refreshData() {
        eventTracker.trackEvent(ReviewersEvents.Refresh)
        viewModelScope.launch {
            _isRefreshing.value = true
            getMergedData()
                .onSuccess {
                    state = state.copy(data = Data.Success(reviewers = it))
                    eventTracker.trackEvent(ReviewersEvents.RefreshSuccess)
                }
                .onFailure {
                    state = state.copy(data = Data.Error)
                    eventTracker.trackEvent(ReviewersEvents.RefreshFailure(it.message ?: "Unrecognised error."))
                }
            _isRefreshing.value = false
        }
    }

    private fun fetchData() {
        eventTracker.trackEvent(ReviewersEvents.Fetch)
        viewModelScope.launch {
            state = state.copy(data = Data.Loading)

            getMergedData()
                .onSuccess {
                    state = state.copy(data = Data.Success(reviewers = it))
                    eventTracker.trackEvent(ReviewersEvents.FetchSuccess)
                }
                .onFailure {
                    state = state.copy(data = Data.Error)
                    eventTracker.trackEvent(ReviewersEvents.FetchFailure(it.message ?: "Unrecognised error."))
                }
        }
    }

    private suspend fun getMergedData(): Result<List<Reviewer>> = downloadData()
        .mapData()

    private suspend fun downloadData(): Pair<Result<RequestedReviewersResponse>, Result<List<Review>>> =
        coroutineScope {
            val requestedReviewersDeferred = async {
                repository.getRequestedReviewers(
                    initialValues.owner,
                    initialValues.repo,
                    initialValues.pullRequestNumber
                )
            }
            val reviewsDeferred = async {
                repository.getReviews(
                    initialValues.owner,
                    initialValues.repo,
                    initialValues.pullRequestNumber
                )
            }

            Pair(requestedReviewersDeferred.await(), reviewsDeferred.await())
        }

    private fun Pair<Result<RequestedReviewersResponse>, Result<List<Review>>>.mapData() =
        mergeData(first.mapRequestedReviewers(), second.mapReviews())

    private fun mergeData(
        requestedReviewers: Result<List<Reviewer>>,
        reviewersWithReviews: Result<List<Reviewer>>
    ) = requestedReviewers.flatMap { list ->
        reviewersWithReviews.map { it + list }
    }

    private fun Result<RequestedReviewersResponse>.mapRequestedReviewers(): Result<List<Reviewer>> =
        map {
            val hoursFromPRStart =
                countHoursTillNow(initialValues.submissionTime)

            it.users.map { requestedReviewer ->
                Reviewer(
                    requestedReviewer.id,
                    requestedReviewer.login,
                    false,
                    hoursFromPRStart,
                    null
                )
            }
        }

    private fun Result<List<Review>>.mapReviews(): Result<List<Reviewer>> {
        val hoursFromPRStart = countHoursTillNow(initialValues.submissionTime)

        return map { list ->
            list.groupBy { it.user.id }
                .map { reviewsForSingleUser ->
                    val latestReview = reviewsForSingleUser.value.minBy { it.submittedAt }
                    val hoursFromReviewDone = countHoursTillNow(latestReview.submittedAt)

                    Reviewer(
                        latestReview.user.id,
                        latestReview.user.login,
                        true,
                        hoursFromPRStart,
                        hoursFromReviewDone
                    )
                }
        }
    }

    private fun countHoursTillNow(submissionTime: Instant): Long =
        Clock.System.now().minus(submissionTime, DateTimeUnit.HOUR)

    fun onAction(action: ReviewersAction) = when (action) {
        is ReviewersAction.Notify -> notifyReviewer(action.userLogin)
        is ReviewersAction.OnSnackbarDismiss -> dismissSnackbar()
        is ReviewersAction.OnTryAgain -> fetchData()
    }

    private fun notifyReviewer(userLogin: String) {
        eventTracker.trackEvent(ReviewersEvents.ClickNotify)
        eventTracker.trackEvent(ReviewersEvents.Notify)
        val (owner, repo, pullRequestNumber) = initialValues
        val successData = state.data as? Data.Success ?: return

        viewModelScope.launch {
            setReviewerToLoading(successData, userLogin)

            repository.notify(owner, repo, pullRequestNumber, "@$userLogin")
                .onSuccess { onNotifyUserSuccess(successData, userLogin) }
                .onFailure { onNotifyUserFailure(successData, userLogin, it.message) }
        }
    }

    private fun setReviewerToLoading(
        successData: Data.Success,
        userLogin: String
    ) {
        state = state.copy(
            data = Data.Success(
                reviewers = successData.reviewers.updateLoadingState(userLogin, true)
            )
        )
    }

    private fun onNotifyUserFailure(
        successData: Data.Success,
        userLogin: String,
        errorMessage: String?
    ) {
        state = state.copy(
            snackbarTypeShown = FAILURE,
            data = Data.Success(
                reviewers = successData.reviewers.updateLoadingState(userLogin, false)
            )
        )
        eventTracker.trackEvent(ReviewersEvents.NotifyFailure(errorMessage ?: "Unrecognised error."))
    }

    private fun onNotifyUserSuccess(
        successData: Data.Success,
        userLogin: String
    ) {
        state = state.copy(
            snackbarTypeShown = SUCCESS,
            data = Data.Success(
                successData.reviewers.updateLoadingState(userLogin, false)
            )
        )
        eventTracker.trackEvent(ReviewersEvents.NotifySuccess)
    }

    private fun List<Reviewer>.updateLoadingState(
        userLogin: String,
        isLoading: Boolean
    ): List<Reviewer> = map {
        if (it.login == userLogin) {
            it.copy(isLoading = isLoading)
        } else {
            it
        }
    }

    private fun dismissSnackbar() {
        state = state.copy(snackbarTypeShown = null)
    }

    fun trackScreenOpened() {
        eventTracker.trackEvent(ReviewersEvents.ScreenOpened)
    }
}
