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

package com.appunite.loudius.ui.pullrequests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.analytics.EventTracker
import com.appunite.loudius.analytics.events.PullRequestsEvents
import com.appunite.loudius.domain.repository.PullRequestRepository
import com.appunite.loudius.network.model.PullRequest
import kotlinx.coroutines.launch

sealed class PulLRequestsAction {
    data class ItemClick(val id: Long) : PulLRequestsAction()
    object OnNavigateToReviewers : PulLRequestsAction()
    object RetryClick : PulLRequestsAction()
}

sealed class Data {
    object Loading : Data()
    object Error : Data()
    data class Success(val pullRequests: List<PullRequest>) : Data()
}

data class PullRequestState(
    val data: Data = Data.Loading,
    val navigateToReviewers: NavigationPayload? = null
)

data class NavigationPayload(
    val owner: String,
    val repo: String,
    val pullRequestNumber: String,
    val submissionTime: String
)

class PullRequestsViewModel(
    private val pullRequestsRepository: PullRequestRepository,
    private val eventTracker: EventTracker
) : ViewModel() {
    var state: PullRequestState by mutableStateOf(PullRequestState())
        private set

    var isRefreshing: Boolean by mutableStateOf(false)
        private set

    init {
        fetchData()
    }

    fun refreshData() {
        eventTracker.trackEvent(PullRequestsEvents.Refresh)
        viewModelScope.launch {
            isRefreshing = true
            pullRequestsRepository.getCurrentUserPullRequests()
                .onSuccess {
                    state = state.copy(data = Data.Success(it.items))
                    eventTracker.trackEvent(PullRequestsEvents.RefreshSuccess)
                }.onFailure {
                    state = state.copy(data = Data.Error)
                    eventTracker.trackEvent(PullRequestsEvents.RefreshFailure(it.message ?: "Unrecognised error."))
                }
            isRefreshing = false
        }
    }

    private fun fetchData() {
        eventTracker.trackEvent(PullRequestsEvents.Fetch)
        viewModelScope.launch {
            state = PullRequestState()
            pullRequestsRepository.getCurrentUserPullRequests()
                .onSuccess {
                    state = state.copy(data = Data.Success(it.items))
                    eventTracker.trackEvent(PullRequestsEvents.FetchSuccess)
                }.onFailure {
                    state = state.copy(data = Data.Error)
                    eventTracker.trackEvent(PullRequestsEvents.FetchFailure(it.message ?: "Unrecognised error."))
                }
        }
    }

    fun onAction(action: PulLRequestsAction) = when (action) {
        is PulLRequestsAction.ItemClick -> navigateToReviewers(action.id)
        is PulLRequestsAction.OnNavigateToReviewers -> resetNavigationState()
        is PulLRequestsAction.RetryClick -> fetchData()
    }

    private fun navigateToReviewers(itemClickedId: Long) {
        val successData = state.data as? Data.Success ?: return
        val index = successData.pullRequests.indexOfFirst { it.id == itemClickedId }
        val itemClickedData = successData.pullRequests[index]
        eventTracker.trackEvent(PullRequestsEvents.NavigateToReviewers)
        state = state.copy(
            navigateToReviewers = NavigationPayload(
                itemClickedData.owner,
                itemClickedData.shortRepositoryName,
                itemClickedData.number.toString(),
                itemClickedData.createdAt.toString()
            )
        )
    }

    private fun resetNavigationState() {
        state = state.copy(navigateToReviewers = null)
    }

    fun trackScreenOpened() {
        eventTracker.trackEvent(PullRequestsEvents.ScreenOpened)
    }
}
