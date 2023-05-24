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

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package com.appunite.loudius.ui.pullrequests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.ui.components.LoudiusFullScreenError
import com.appunite.loudius.ui.components.LoudiusListIcon
import com.appunite.loudius.ui.components.LoudiusListItem
import com.appunite.loudius.ui.components.LoudiusLoadingIndicator
import com.appunite.loudius.ui.components.LoudiusPlaceholderText
import com.appunite.loudius.ui.components.LoudiusText
import com.appunite.loudius.ui.components.LoudiusTextStyle
import com.appunite.loudius.ui.components.LoudiusTopAppBar

typealias NavigateToReviewers = (String, String, String, String) -> Unit

@Composable
fun PullRequestsScreen(
    viewModel: PullRequestsViewModel = hiltViewModel(),
    navigateToReviewers: NavigateToReviewers,
) {
    val state = viewModel.state
    val swipeRefreshState = rememberPullRefreshState(
        refreshing = state.data == Data.Loading,
        onRefresh = { viewModel.fetchData() },
    )
    PullRequestsScreenStateless(
        state = state,
        pullRefreshState = swipeRefreshState,
        onAction = viewModel::onAction,
    )
    LaunchedEffect(state.navigateToReviewers) {
        navigateToReviewers(state, navigateToReviewers, viewModel)
    }
}

private fun navigateToReviewers(
    state: PullRequestState,
    navigateToReviewers: NavigateToReviewers,
    viewModel: PullRequestsViewModel,
) {
    state.navigateToReviewers?.let {
        navigateToReviewers(
            it.owner,
            it.repo,
            it.pullRequestNumber,
            it.submissionTime,
        )
        viewModel.onAction(PulLRequestsAction.OnNavigateToReviewers)
    }
}

@Composable
private fun PullRequestsScreenStateless(
    state: PullRequestState,
    pullRefreshState: PullRefreshState,
    onAction: (PulLRequestsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            LoudiusTopAppBar(title = stringResource(R.string.app_name))
        },
        content = { padding ->
            when (state.data) {
                is Data.Error -> LoudiusFullScreenError(
                    modifier = Modifier.padding(padding),
                    onButtonClick = { onAction(PulLRequestsAction.RetryClick) },
                )
                is Data.Loading -> LoudiusLoadingIndicator(Modifier.padding(padding))
                is Data.Success -> PullRequestContent(state.data, pullRefreshState, padding, onAction)
            }
        },
    )
}

@Composable
private fun PullRequestContent(
    state: Data,
    pullRefreshState: PullRefreshState,
    padding: PaddingValues,
    onAction: (PulLRequestsAction) -> Unit,
) {
    if ((state as Data.Success).pullRequests.isEmpty()) {
        EmptyListPlaceholder(padding)
    } else {
        PullRequestsList(
            pullRequests = state.pullRequests,
            modifier = Modifier.padding(padding),
            onItemClick = onAction,
            pullRefreshState = pullRefreshState,
            isLoading = state == Data.Loading,
        )
    }
}

@Composable
private fun PullRequestsList(
    pullRequests: List<PullRequest>,
    modifier: Modifier,
    onItemClick: (PulLRequestsAction) -> Unit,
    pullRefreshState: PullRefreshState,
    isLoading: Boolean,
) {
    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
        ) {
            itemsIndexed(pullRequests) { index, item ->
                PullRequestItem(
                    index = index,
                    data = item,
                    onClick = onItemClick,
                )
            }
        }
        PullRefreshIndicator(isLoading, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun PullRequestItem(
    index: Int,
    data: PullRequest,
    onClick: (PulLRequestsAction) -> Unit,
) {
    LoudiusListItem(
        index = index,
        modifier = Modifier
            .clickable { onClick(PulLRequestsAction.ItemClick(data.id)) },
        icon = { modifier -> PullRequestIcon(modifier) },
        content = { modifier ->
            RepoDetails(
                modifier = modifier,
                pullRequestTitle = data.title,
                repositoryName = data.fullRepositoryName,
            )
        },
    )
}

@Composable
private fun PullRequestIcon(modifier: Modifier) {
    LoudiusListIcon(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_pull_request),
        contentDescription = stringResource(id = R.string.pull_requests_screen_pull_request_content_description),
    )
}

@Composable
private fun RepoDetails(modifier: Modifier, pullRequestTitle: String, repositoryName: String) {
    Column(modifier = modifier) {
        LoudiusText(
            text = pullRequestTitle,
            style = LoudiusTextStyle.ListItem,
        )
        LoudiusText(
            text = repositoryName,
            style = LoudiusTextStyle.ListCaption,
        )
    }
}

@Composable
private fun EmptyListPlaceholder(padding: PaddingValues) {
    Box(modifier = Modifier.padding(padding)) {
        LoudiusPlaceholderText(
            textId = R.string.you_dont_have_any_pull_request,
        )
    }
}

// @Preview("Pull requests - filled list")
// @Composable
// fun PullRequestsScreenPreview() {
//    LoudiusTheme {
//        PullRequestsScreenStateless(
//            state = PullRequestState(
//                Data.Success(
//                    listOf(
//                        PullRequest(
//                            id = 0,
//                            draft = false,
//                            number = 0,
//                            repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Stefan",
//                            title = "[SIL-67] Details screen - network layer",
//                            createdAt = LocalDateTime.parse("2021-11-29T16:31:41"),
//                        ),
//                        PullRequest(
//                            id = 1,
//                            draft = true,
//                            number = 1,
//                            repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Silentus",
//                            title = "[SIL-66] Add client secret to build config",
//                            createdAt = LocalDateTime.parse("2022-11-29T16:31:41"),
//                        ),
//                        PullRequest(
//                            id = 2,
//                            draft = false,
//                            number = 2,
//                            repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Loudius",
//                            title = "[SIL-73] Storing access token",
//                            createdAt = LocalDateTime.parse("2023-01-29T16:31:41"),
//                        ),
//                        PullRequest(
//                            id = 3,
//                            draft = false,
//                            number = 3,
//                            repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Blocktrade",
//                            title = "[SIL-62/SIL-75] Provide new annotation for API instances",
//                            createdAt = LocalDateTime.parse("2022-01-29T16:31:41"),
//                        ),
//                    ),
//                ),
//            ),
//            onAction = {},
//        )
//    }
// }
//
// @Preview("Pull requests - empty list")
// @Composable
// fun PullRequestsScreenEmptyListPreview() {
//    LoudiusTheme {
//        PullRequestsScreenStateless(
//            PullRequestState(Data.Success(emptyList())),
//            onAction = {},
//        )
//    }
// }
//
// @Preview("Pull requests - Loading")
// @Composable
// fun PullRequestsScreenLoadingPreview() {
//    LoudiusTheme {
//        PullRequestsScreenStateless(
//            PullRequestState(Data.Loading),
//            onAction = {},
//        )
//    }
// }
//
// @Preview("Pull requests - Error")
// @Composable
// fun PullRequestsScreenErrorPreview() {
//    LoudiusTheme {
//        PullRequestsScreenStateless(
//            PullRequestState(Data.Error),
//            onAction = {},
//        )
//    }
// }
