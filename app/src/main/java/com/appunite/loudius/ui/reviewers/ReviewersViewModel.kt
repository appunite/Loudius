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
import com.appunite.loudius.common.Screen.Reviewers.getInitialValues
import com.appunite.loudius.common.flatMap
import com.appunite.loudius.domain.repository.PullRequestRepository
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.FAILURE
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

sealed class ReviewersAction {
    data class Notify(val userLogin: String) : ReviewersAction()
    object OnSnackbarDismiss : ReviewersAction()
    object OnTryAgain : ReviewersAction()
}

data class ReviewersState(
    val reviewers: List<Reviewer> = emptyList(),
    val pullRequestNumber: String = "",
    val snackbarTypeShown: ReviewersSnackbarType? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)

enum class ReviewersSnackbarType {
    SUCCESS, FAILURE
}

@HiltViewModel
class ReviewersViewModel @Inject constructor(
    private val repository: PullRequestRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val initialValues = getInitialValues(savedStateHandle)

    var state by mutableStateOf(ReviewersState())
        private set

    init {
        state = state.copy(pullRequestNumber = initialValues.pullRequestNumber)
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, isError = false)

            getMergedData()
                .onSuccess { state = state.copy(reviewers = it, isLoading = false) }
                .onFailure { state = state.copy(isError = true, isLoading = false) }
        }
    }

    private suspend fun getMergedData(): Result<List<Reviewer>> = downloadData()
        .mapData()


    private suspend fun downloadData(): Pair<Result<RequestedReviewersResponse>, Result<List<Review>>> =
        coroutineScope {
            val (owner, repo, pullRequestNumber) = initialValues

            val requestedReviewersDeferred =
                async { repository.getRequestedReviewers(owner, repo, pullRequestNumber) }
            val reviewsDeferred = async { repository.getReviews(owner, repo, pullRequestNumber) }

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
                countHoursTillNow(LocalDateTime.parse(initialValues.submissionTime))

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
        val hoursFromPRStart = countHoursTillNow(LocalDateTime.parse(initialValues.submissionTime))

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
                        hoursFromReviewDone,
                    )
                }
        }
    }

    private fun countHoursTillNow(submissionTime: LocalDateTime): Long =
        ChronoUnit.HOURS.between(submissionTime, LocalDateTime.now())

    fun onAction(action: ReviewersAction) = when (action) {
        is ReviewersAction.Notify -> notifyUser(action.userLogin)
        is ReviewersAction.OnSnackbarDismiss -> dismissSnackbar()
        is ReviewersAction.OnTryAgain -> fetchData()
    }

    private fun notifyUser(userLogin: String) {
        val (owner, repo, pullRequestNumber) = initialValues

        viewModelScope.launch {
            state = state.copy(reviewers = updateReviewerLoadingState(userLogin, true))
            repository.notify(owner, repo, pullRequestNumber, "@$userLogin")
                .onSuccess {
                    state = state.copy(
                        snackbarTypeShown = SUCCESS,
                        reviewers = updateReviewerLoadingState(userLogin, false),
                    )
                }
                .onFailure {
                    state = state.copy(
                        snackbarTypeShown = FAILURE,
                        reviewers = updateReviewerLoadingState(userLogin, false),
                    )
                }
        }
    }

    private fun updateReviewerLoadingState(userLogin: String, isLoading: Boolean) =
        state.reviewers.map { if (it.login == userLogin) it.copy(isLoading = isLoading) else it }

    private fun dismissSnackbar() {
        state = state.copy(snackbarTypeShown = null)
    }
}
