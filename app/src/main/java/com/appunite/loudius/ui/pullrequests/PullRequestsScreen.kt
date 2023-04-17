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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.appunite.loudius.ui.pullrequests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.common.Constants
import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.ui.components.LoudiusFullScreenError
import com.appunite.loudius.ui.components.LoudiusListIcon
import com.appunite.loudius.ui.components.LoudiusListItem
import com.appunite.loudius.ui.components.LoudiusLoadingIndicator
import com.appunite.loudius.ui.components.LoudiusPlaceholderText
import com.appunite.loudius.ui.components.LoudiusText
import com.appunite.loudius.ui.components.LoudiusTextStyle
import com.appunite.loudius.ui.components.LoudiusTopAppBar
import com.appunite.loudius.ui.theme.LoudiusTheme
import java.time.LocalDateTime

typealias NavigateToReviewers = (String, String, String, String) -> Unit

@Composable
fun PullRequestsScreen(
    viewModel: PullRequestsViewModel = hiltViewModel(),
    navigateToReviewers: NavigateToReviewers,
) {
    val state = viewModel.state
    PullRequestsScreenStateless(
        state = state,
        onAction = viewModel::onAction,
        navigateToReviewers = navigateToReviewers,
    )
}

@Composable
private fun PullRequestsScreenStateless(
    state: PullRequestState,
    onAction: (PulLRequestsAction) -> Unit,
    navigateToReviewers: NavigateToReviewers,
) {
    Scaffold(
        topBar = {
            LoudiusTopAppBar(title = stringResource(R.string.app_name))
        },
        content = { padding ->
            when (state) {
                is PullRequestState.Error -> LoudiusFullScreenError(
                    modifier = Modifier.padding(padding),
                    onButtonClick = { onAction(PulLRequestsAction.RetryClick) },
                )

                is PullRequestState.Loading -> LoudiusLoadingIndicator(Modifier.padding(padding))
                is PullRequestState.Loaded -> {
                    PullRequestContent(state, padding, onAction)
                    LaunchedEffect(state.navigateToReviewers) {
                        state.navigateToReviewers?.let {
                            navigateToReviewers(
                                it.owner,
                                it.repo,
                                it.pullRequestNumber,
                                it.submissionTime,
                            )
                            onAction(PulLRequestsAction.OnNavigateToReviewers)
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun PullRequestContent(
    state: PullRequestState.Loaded,
    padding: PaddingValues,
    onAction: (PulLRequestsAction) -> Unit,
) {
    if (state.pullRequests.isEmpty()) {
        EmptyListPlaceholder(padding)
    } else {
        PullRequestsList(
            pullRequests = state.pullRequests,
            modifier = Modifier.padding(padding),
            onItemClick = onAction,
        )
    }
}

@Composable
private fun PullRequestsList(
    pullRequests: List<PullRequest>,
    modifier: Modifier,
    onItemClick: (PulLRequestsAction) -> Unit,
) {
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
}

@Composable
private fun PullRequestItem(
    index: Int,
    data: PullRequest,
    onClick: (PulLRequestsAction) -> Unit,
) {
    LoudiusListItem(
        index = index,
        modifier = Modifier.clickable { onClick(PulLRequestsAction.ItemClick(data.id)) },
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

@Preview("Pull requests - filled list")
@Composable
fun PullRequestsScreenPreview() {
    LoudiusTheme {
        PullRequestsScreenStateless(
            state = PullRequestState.Loaded(
                listOf(
                    PullRequest(
                        id = 0,
                        draft = false,
                        number = 0,
                        repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Stefan",
                        title = "[SIL-67] Details screen - network layer",
                        createdAt = LocalDateTime.parse("2021-11-29T16:31:41"),
                    ),
                    PullRequest(
                        id = 1,
                        draft = true,
                        number = 1,
                        repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Silentus",
                        title = "[SIL-66] Add client secret to build config",
                        createdAt = LocalDateTime.parse("2022-11-29T16:31:41"),
                    ),
                    PullRequest(
                        id = 2,
                        draft = false,
                        number = 2,
                        repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Loudius",
                        title = "[SIL-73] Storing access token",
                        createdAt = LocalDateTime.parse("2023-01-29T16:31:41"),
                    ),
                    PullRequest(
                        id = 3,
                        draft = false,
                        number = 3,
                        repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Blocktrade",
                        title = "[SIL-62/SIL-75] Provide new annotation for API instances",
                        createdAt = LocalDateTime.parse("2022-01-29T16:31:41"),
                    ),
                ),
            ),
            onAction = {},
            navigateToReviewers = { _, _, _, _ -> },
        )
    }
}

@Preview("Pull requests - empty list")
@Composable
fun PullRequestsScreenEmptyListPreview() {
    LoudiusTheme {
        PullRequestsScreenStateless(
            PullRequestState.Loaded(emptyList()),
            onAction = {},
            navigateToReviewers = { _, _, _, _ -> },
        )
    }
}

@Preview("Pull requests - Loading")
@Composable
fun PullRequestsScreenLoadingPreview() {
    LoudiusTheme {
        PullRequestsScreenStateless(
            PullRequestState.Loading,
            onAction = {},
            navigateToReviewers = { _, _, _, _ -> },
        )
    }
}

@Preview("Pull requests - Error")
@Composable
fun PullRequestsScreenErrorPreview() {
    LoudiusTheme {
        PullRequestsScreenStateless(
            PullRequestState.Error,
            onAction = {},
            navigateToReviewers = { _, _, _, _ -> },
        )
    }
}
