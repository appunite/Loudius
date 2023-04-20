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

sealed class Data {
    object Loading : Data()
    object Error : Data()
    data class Loaded(val pullRequests: List<PullRequest>) : Data()
}

data class PullRequestState(
    val data: Data = Data.Loading,
    val navigateToReviewers: NavigationPayload? = null,
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
    var state: PullRequestState by mutableStateOf(PullRequestState())
        private set

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            state = PullRequestState()
            pullRequestsRepository.getCurrentUserPullRequests()
                .onSuccess {
                    state = state.copy(data = Data.Loaded(it.items))
                }.onFailure {
                    state = state.copy(data = Data.Error)
                }
        }
    }

    fun onAction(action: PulLRequestsAction) = when (action) {
        is PulLRequestsAction.ItemClick -> navigateToReviewers(action.id)
        is PulLRequestsAction.OnNavigateToReviewers -> resetNavigationState()
        is PulLRequestsAction.RetryClick -> fetchData()
    }

    private fun navigateToReviewers(itemClickedId: Int) {
        val loadedData = state.data as? Data.Loaded ?: return
        val index = loadedData.pullRequests.indexOfFirst { it.id == itemClickedId }
        val itemClickedData = loadedData.pullRequests[index]
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
