package com.appunite.loudius.ui.pullrequests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.domain.PullRequestsRepository
import com.appunite.loudius.network.model.PullRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PullRequestState(
    val pullRequests: List<PullRequest> = emptyList(),
)

@HiltViewModel
class PullRequestsViewModel @Inject constructor(
    private val pullRequestsRepository: PullRequestsRepository,
) : ViewModel() {
    var state by mutableStateOf(PullRequestState())
        private set

    init {
        viewModelScope.launch {
            pullRequestsRepository.getCurrentUserPullRequests()
                .onSuccess {
                    state = state.copy(pullRequests = it.items)
                }
        }
    }
}
