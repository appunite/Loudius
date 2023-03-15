package com.appunite.loudius.ui.reviewers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.common.Screen
import com.appunite.loudius.domain.PullRequestRepository
import com.appunite.loudius.domain.model.Reviewer
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.launch

sealed class ReviewersAction {
    data class Notify(val userLogin: String) : ReviewersAction()
    object OnSnackbarDismiss : ReviewersAction()
}

data class ReviewersState(
    val reviewers: List<Reviewer> = emptyList(),
    val pullRequestNumber: String = "",
    val showSuccessSnackbar: Unit? = null,
)

@HiltViewModel
class ReviewersViewModel @Inject constructor(
    private val repository: PullRequestRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var state by mutableStateOf(ReviewersState())
        private set

    init {
        val initialValues = getInitialValues(savedStateHandle)

        state = state.copy(pullRequestNumber = initialValues.pullRequestNumber)

        viewModelScope.launch {
            fetchRequestedReviewers(initialValues)
            fetchReviews(initialValues)
        }
    }

    private fun getInitialValues(savedStateHandle: SavedStateHandle) = InitialValues(
        checkNotNull(savedStateHandle[Screen.Reviewers.ownerArg]),
        checkNotNull(savedStateHandle[Screen.Reviewers.repoArg]),
        checkNotNull(savedStateHandle[Screen.Reviewers.pullRequestNumberArg]),
        checkNotNull(savedStateHandle[Screen.Reviewers.submissionDateArg]),
    )

    private suspend fun fetchRequestedReviewers(initialValues: InitialValues) {
        val (owner, repo, pullRequestNumber, submissionTime) = initialValues

        repository.getRequestedReviewers(owner, repo, pullRequestNumber)
            .onSuccess { response ->
                val reviewers = response.mapToReviewers(submissionTime)
                state = state.copy(reviewers = state.reviewers + reviewers)
            }
    }

    private fun RequestedReviewersResponse.mapToReviewers(submissionTime: String): List<Reviewer> {
        val hoursFromPRStart = countHoursTillNow(LocalDateTime.parse(submissionTime))

        return users.map {
            Reviewer(it.id, it.login, false, hoursFromPRStart, null)
        }
    }

    private suspend fun fetchReviews(initialValues: InitialValues) {
        val (owner, repo, pullRequestNumber, submissionTime) = initialValues

        repository.getReviews(owner, repo, pullRequestNumber).onSuccess { reviews ->
            val reviewersAfterReview = reviews.mapToReviewers(submissionTime)
            state = state.copy(reviewers = state.reviewers + reviewersAfterReview)
        }
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
        is ReviewersAction.OnSnackbarDismiss -> state = state.copy(showSuccessSnackbar = null)
    }

    private fun notifyUser(userLogin: String) {
        val (owner, repo, pullRequestNumber) = getInitialValues(savedStateHandle)

        viewModelScope.launch {
            repository.notify(owner, repo, pullRequestNumber, "@$userLogin")
                .onSuccess {
                    state = state.copy(showSuccessSnackbar = Unit)
                }
        }
    }

    private data class InitialValues(
        val owner: String,
        val repo: String,
        val pullRequestNumber: String,
        val submissionTime: String,
    )
}
