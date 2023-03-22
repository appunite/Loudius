package com.appunite.loudius.ui.reviewers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.common.Screen
import com.appunite.loudius.common.flatMap
import com.appunite.loudius.domain.PullRequestRepository
import com.appunite.loudius.domain.model.Reviewer
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.FAILURE
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

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
    private val initialValues: InitialValues = getInitialValues(savedStateHandle)

    var state by mutableStateOf(ReviewersState())
        private set

    init {
        state = state.copy(pullRequestNumber = initialValues.pullRequestNumber)
        fetchData()
    }

    private fun getInitialValues(savedStateHandle: SavedStateHandle) = InitialValues(
        checkNotNull(savedStateHandle[Screen.Reviewers.ownerArg]),
        checkNotNull(savedStateHandle[Screen.Reviewers.repoArg]),
        checkNotNull(savedStateHandle[Screen.Reviewers.pullRequestNumberArg]),
        checkNotNull(savedStateHandle[Screen.Reviewers.submissionDateArg]),
    )

    private fun fetchData() {
        viewModelScope.launch {
            getMergedData()
                .onSuccess { state = state.copy(reviewers = it.orEmpty(), isLoading = false) }
                .onFailure { state = state.copy(isError = true, isLoading = false) }
        }
    }

    private suspend fun getMergedData(): Result<List<Reviewer>?> =
        coroutineScope {
            state = state.copy(isLoading = true, isError = false)
            val requestedReviewersDeferred = async { fetchRequestedReviewers() }
            val reviewersDeferred = async { fetchReviews() }

            val requestedReviewerResult = requestedReviewersDeferred.await()
            val reviewersResult = reviewersDeferred.await()

            requestedReviewerResult.flatMap { requestedReviewers ->
                reviewersResult.map { it + requestedReviewers }
            }
        }

    private suspend fun fetchRequestedReviewers(): Result<List<Reviewer>> {
        val (owner, repo, pullRequestNumber, submissionTime) = initialValues

        return repository.getRequestedReviewers(owner, repo, pullRequestNumber)
            .map { it.mapToReviewers(submissionTime) }
    }

    private fun RequestedReviewersResponse.mapToReviewers(submissionTime: String): List<Reviewer> {
        val hoursFromPRStart = countHoursTillNow(LocalDateTime.parse(submissionTime))

        return users.map {
            Reviewer(it.id, it.login, false, hoursFromPRStart, null)
        }
    }

    private suspend fun fetchReviews(): Result<List<Reviewer>> {
        val (owner, repo, pullRequestNumber, submissionTime) = initialValues

        return repository.getReviews(owner, repo, pullRequestNumber)
            .map { it.mapToReviewers(submissionTime) }
    }

    private fun List<Review>.mapToReviewers(submissionTime: String): List<Reviewer> {
        val hoursFromPRStart = countHoursTillNow(LocalDateTime.parse(submissionTime))

        return groupBy { it.user.id }
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
            repository.notify(owner, repo, pullRequestNumber, "@$userLogin")
                .onSuccess { state = state.copy(snackbarTypeShown = SUCCESS) }
                .onFailure { state = state.copy(snackbarTypeShown = FAILURE) }
        }
    }

    private fun dismissSnackbar() {
        state = state.copy(snackbarTypeShown = null)
    }

    private data class InitialValues(
        val owner: String,
        val repo: String,
        val pullRequestNumber: String,
        val submissionTime: String,
    )
}
