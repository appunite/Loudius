@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.appunite.loudius.ui.pullrequests

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.common.Constants
import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.ui.components.LoudiusTopAppBar

@Composable
fun PullRequestsScreen(viewModel: PullRequestsViewModel = hiltViewModel()) {
    val state = viewModel.state
    PullRequestsScreenStateless(pullRequests = state.pullRequests)
}

@Composable
private fun PullRequestsScreenStateless(
    pullRequests: List<PullRequest>,
) {
    Scaffold(topBar = {
        LoudiusTopAppBar(title = stringResource(R.string.app_name)) {
            // TODO: navigation
        }
    }, content = { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            items(pullRequests) {
                PullRequestItem(
                    repositoryName = it.fullRepositoryName,
                    pullRequestTitle = it.title,
                )
            }
        }
    })
}

@Composable
private fun PullRequestItem(repositoryName: String, pullRequestTitle: String) {
    Text(text = repositoryName)
    Text(text = pullRequestTitle)
    Spacer(modifier = Modifier.height(8.dp))
}

@Preview("Pull requests - empty list")
@Composable
fun PullRequestsScreenEmptyListPreview() {
    PullRequestsScreenStateless(emptyList())
}

@Preview("Pull requests - filled list")
@Composable
fun PullRequestsScreenPreview() {
    PullRequestsScreenStateless(
        listOf(
            PullRequest(
                id = 0,
                draft = false,
                number = 0,
                repositoryUrl = "${Constants.BASE_API_URL}/appunite/Stefan",
                title = "PR 1",
                updatedAt = "2021-11-29T16:31:41Z",
            ),
            PullRequest(
                id = 1,
                draft = true,
                number = 1,
                repositoryUrl = "${Constants.BASE_API_URL}/appunite/Silentus",
                title = "PR 2",
                updatedAt = "2022-11-29T16:31:41Z",
            ),
            PullRequest(
                id = 2,
                draft = false,
                number = 2,
                repositoryUrl = "${Constants.BASE_API_URL}/appunite/Loudius",
                title = "PR 3",
                updatedAt = "2023-01-29T16:31:41Z",
            ),
            PullRequest(
                id = 3,
                draft = false,
                number = 3,
                repositoryUrl = "${Constants.BASE_API_URL}/appunite/Blocktrade",
                title = "PR 4",
                updatedAt = "2022-01-29T16:31:41Z",
            ),
        ),
    )
}
