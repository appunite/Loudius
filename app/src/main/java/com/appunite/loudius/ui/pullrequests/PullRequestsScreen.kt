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
    LaunchedEffect(state.navigateToReviewers) {
        state.navigateToReviewers?.let {
            navigateToReviewers(it.owner, it.repo, it.pullRequestNumber, it.submissionTime)
            viewModel.onAction(PulLRequestsAction.OnNavigateToReviewers)
        }
    }
    PullRequestsScreenStateless(
        pullRequests = state.pullRequests,
        onAction = viewModel::onAction,
        isLoading = state.isLoading,
        isError = state.isError,
    )
}

@Composable
private fun PullRequestsScreenStateless(
    pullRequests: List<PullRequest>,
    onAction: (PulLRequestsAction) -> Unit,
    isLoading: Boolean,
    isError: Boolean,
) {
    Scaffold(
        topBar = {
            LoudiusTopAppBar(title = stringResource(R.string.app_name))
        },
        content = { padding ->
            when {
                isError -> LoudiusFullScreenError(
                    onButtonClick = { onAction(PulLRequestsAction.RetryClick) },
                )

                isLoading -> LoudiusLoadingIndicator()
                pullRequests.isEmpty() -> EmptyListPlaceholder(padding)
                else -> PullRequestsList(
                    pullRequests = pullRequests,
                    modifier = Modifier.padding(padding),
                    onItemClick = onAction,
                )
            }
        },
    )
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
            isLoading = false,
            isError = false,
            pullRequests = listOf(
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
            onAction = {},
        )
    }
}

@Preview("Pull requests - empty list")
@Composable
fun PullRequestsScreenEmptyListPreview() {
    LoudiusTheme {
        PullRequestsScreenStateless(
            emptyList(),
            isLoading = false,
            isError = false,
            onAction = {},
        )
    }
}

@Preview("Pull requests - Loading")
@Composable
fun PullRequestsScreenLoadingPreview() {
    LoudiusTheme {
        PullRequestsScreenStateless(
            emptyList(),
            isLoading = true,
            isError = false,
            onAction = {},
        )
    }
}

@Preview("Pull requests - Error")
@Composable
fun PullRequestsScreenErrorPreview() {
    LoudiusTheme {
        PullRequestsScreenStateless(
            emptyList(),
            isLoading = false,
            isError = true,
            onAction = {},
        )
    }
}
