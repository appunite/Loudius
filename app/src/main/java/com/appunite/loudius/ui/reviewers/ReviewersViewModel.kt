package com.appunite.loudius.ui.reviewers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.common.Screen
import com.appunite.loudius.domain.GitHubPullRequestsRepository
import com.appunite.loudius.domain.model.Reviewer
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.launch

data class ReviewersState(
    val reviewers: List<Reviewer> = emptyList(),
    val pullRequestNumber: String = "",
)

@HiltViewModel
class ReviewersViewModel @Inject constructor(
    private val repository: GitHubPullRequestsRepository,
    savedStateHandle: SavedStateHandle,
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

    private fun getInitialValues(savedStateHandle: SavedStateHandle): InitialValues {
        val owner: String = checkNotNull(savedStateHandle[Screen.Reviewers.ownerArg])
        val repo: String = checkNotNull(savedStateHandle[Screen.Reviewers.repoArg])
        val pullRequestNumber: String =
            checkNotNull(savedStateHandle[Screen.Reviewers.pullRequestNumberArg])
        val submissionDate: String =
            checkNotNull(savedStateHandle[Screen.Reviewers.submissionDateArg])
        return InitialValues(owner, repo, pullRequestNumber, submissionDate)
    }

    private suspend fun fetchRequestedReviewers(
        initialValues: InitialValues
    ) {
        val (owner, repo, pullRequestNumber, submissionTime) = initialValues
        val hoursFromPRStart = countHoursTillNow(LocalDateTime.parse(submissionTime))
        repository.getReviewers(owner, repo, pullRequestNumber).onSuccess { response ->
            val reviewers = response.users.map {
                Reviewer(it.id, it.login, false, hoursFromPRStart, null)
            }
            state = state.copy(reviewers = state.reviewers + reviewers)
        }
    }

    private suspend fun fetchReviews(initialValues: InitialValues) {
        val (owner, repo, pullRequestNumber, submissionTime) = initialValues
        val hoursFromPRStart = countHoursTillNow(LocalDateTime.parse(submissionTime))
        repository.getReviews(owner, repo, pullRequestNumber).onSuccess { reviews ->
            reviews.groupBy { it.user.id }.map { singleUser ->
                val latestReview = singleUser.value.minBy { it.submittedAt }
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

    private fun countHoursTillNow(submissionTime: LocalDateTime): Long =
        Duration.between(submissionTime, LocalDateTime.now()).toHours()


    private data class InitialValues(
        val owner: String,
        val repo: String,
        val pullRequestNumber: String,
        val submissionTime: String,
    )
}
