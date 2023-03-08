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
    private val pullRequestNumber: String =
        checkNotNull(savedStateHandle[Screen.Reviewers.pullRequestNumberArg])
    private val owner: String = checkNotNull(savedStateHandle[Screen.Reviewers.ownerArg])
    private val repo: String = checkNotNull(savedStateHandle[Screen.Reviewers.repoArg])
    private val submissionDate: String =
        checkNotNull(savedStateHandle[Screen.Reviewers.submissionDateArg])

    var state by mutableStateOf(ReviewersState())
        private set

    init {
        viewModelScope.launch {
            fetchRequestedReviewers(owner, repo, pullRequestNumber)
            fetchReviews(owner, repo, pullRequestNumber)
        }
    }


    private suspend fun fetchRequestedReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String
    ) {
        repository.getReviewers(owner, repo, pullRequestNumber).onSuccess { response ->
            val reviewers = response.users.map {
                Reviewer(it.id, it.login, false, 10, null)
            }
            state = state.copy(reviewers = state.reviewers + reviewers)
        }
    }

    private suspend fun fetchReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String
    ) {
        repository.getReviews(owner, repo, pullRequestNumber).onSuccess { reviews ->
            reviews.groupBy { it.user.id }.map { singleUser ->
                val latestReview = singleUser.value.minBy { it.submittedAt }

                Reviewer(latestReview.user.id, latestReview.user.login, true, 10, 10)
            }
        }
    }
}
