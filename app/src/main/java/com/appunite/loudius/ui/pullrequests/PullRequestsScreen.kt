@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.appunite.loudius.ui.pullrequests

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            pullRequests.forEach {
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
                0,
                false,
                0,
                "${Constants.BASE_API_URL}/appunite/Stefan",
                "PR 1",
                "2021-11-29T16:31:41Z",
            ),
            PullRequest(
                1,
                true,
                1,
                "${Constants.BASE_API_URL}/appunite/Silentus",
                "PR 2",
                "2022-11-29T16:31:41Z",
            ),
            PullRequest(
                2,
                false,
                2,
                "${Constants.BASE_API_URL}/appunite/Loudius",
                "PR 3",
                "2023-01-29T16:31:41Z",
            ),
            PullRequest(
                3,
                false,
                3,
                "${Constants.BASE_API_URL}/appunite/Blocktrade",
                "PR 4",
                "2022-01-29T16:31:41Z",
            ),
        ),
    )
}
