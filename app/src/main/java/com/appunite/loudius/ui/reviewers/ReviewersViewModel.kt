package com.appunite.loudius.ui.reviewers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.domain.GitHubPullRequestsRepository
import com.appunite.loudius.domain.model.Reviewer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

data class ReviewersState(
    val reviewers: List<Reviewer> = emptyList()
)

@HiltViewModel
class ReviewersViewModel @Inject constructor(
    private val repository: GitHubPullRequestsRepository
) : ViewModel() {
    var state by mutableStateOf(ReviewersState())
        private set

    init {

        viewModelScope.launch {
            fetchRequestedReviewers()
            fetchReviews()
        }
    }


    private suspend fun fetchRequestedReviewers() {
        repository.getReviewers("Appunite", "Loudius", "19").onSuccess { response ->
            val reviewers = response.users.map {
                Reviewer(it.id, it.login, false, 10, null)
            }
            state = state.copy(reviewers = state.reviewers + reviewers)
        }
    }

    private suspend fun fetchReviews() {
        repository.getReviews("Appunite", "Loudius", "19").onSuccess { reviews ->
            reviews.groupBy { it.user.id }.map { singleUser ->
                val latestReview = singleUser.value.minBy { it.submittedAt }

                Reviewer(latestReview.user.id, latestReview.user.login, true, 10, 10)
            }
        }
    }
}
