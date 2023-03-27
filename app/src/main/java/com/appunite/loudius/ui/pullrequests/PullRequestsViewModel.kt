package com.appunite.loudius.ui.pullrequests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.domain.repository.PullRequestRepository
import com.appunite.loudius.network.model.PullRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PulLRequestsAction {
    data class ItemClick(val id: Int) : PulLRequestsAction()
    object OnNavigateToReviewers : PulLRequestsAction()
    object RetryClick : PulLRequestsAction()
}

data class PullRequestState(
    val pullRequests: List<PullRequest> = emptyList(),
    val navigateToReviewers: NavigationPayload? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)

data class NavigationPayload(
    val owner: String,
    val repo: String,
    val pullRequestNumber: String,
    val submissionTime: String,
)

@HiltViewModel
class PullRequestsViewModel @Inject constructor(
    private val pullRequestsRepository: PullRequestRepository,
) : ViewModel() {
    var state by mutableStateOf(PullRequestState())
        private set

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, isError = false)
            pullRequestsRepository.getCurrentUserPullRequests()
                .onSuccess {
                    state = state.copy(pullRequests = it.items, isLoading = false)
                }.onFailure {
                    state = state.copy(isLoading = false, isError = true)
                }
        }
    }

    fun onAction(action: PulLRequestsAction) = when (action) {
        is PulLRequestsAction.ItemClick -> navigateToReviewers(action.id)
        is PulLRequestsAction.OnNavigateToReviewers -> resetNavigationState()
        is PulLRequestsAction.RetryClick -> fetchData()
    }

    private fun navigateToReviewers(itemClickedId: Int) {
        val index = state.pullRequests.indexOfFirst { it.id == itemClickedId }
        val itemClickedData = state.pullRequests[index]
        state = state.copy(
            navigateToReviewers = NavigationPayload(
                itemClickedData.owner,
                itemClickedData.shortRepositoryName,
                itemClickedData.number.toString(),
                itemClickedData.createdAt.toString(),
            ),
        )
    }

    private fun resetNavigationState() {
        state = state.copy(navigateToReviewers = null)
    }
}
