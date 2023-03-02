@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.appunite.loudius.ui.pullrequests

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
        LoudiusTopAppBar(title = stringResource(R.string.app_name))
    }, content = { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            itemsIndexed(pullRequests) { index, item ->
                val isIndexEven = index % 2 == 0
                PullRequestItem(
                    repositoryName = item.fullRepositoryName,
                    pullRequestTitle = item.title,
                    darkBackground = isIndexEven,
                    onClick = {},
                )
            }
        }
    })
}

@Composable
private fun PullRequestItem(
    repositoryName: String,
    pullRequestTitle: String,
    darkBackground: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (darkBackground) {
        MaterialTheme.colorScheme.onSurface.copy(0.08f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_share),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 19.dp, top = 10.dp)
                .size(width = 18.dp, height = 20.dp),
        )
        Column(Modifier.padding(start = 19.dp, top = 8.dp, bottom = 8.dp)) {
            Text(text = pullRequestTitle, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = repositoryName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    Divider(color = MaterialTheme.colorScheme.outlineVariant)
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
                repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Stefan",
                title = "[SIL-67] Details screen - network layer",
                updatedAt = "2021-11-29T16:31:41Z",
            ),
            PullRequest(
                id = 1,
                draft = true,
                number = 1,
                repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Silentus",
                title = "[SIL-66] Add client secret to build config",
                updatedAt = "2022-11-29T16:31:41Z",
            ),
            PullRequest(
                id = 2,
                draft = false,
                number = 2,
                repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Loudius",
                title = "[SIL-73] Storing access token",
                updatedAt = "2023-01-29T16:31:41Z",
            ),
            PullRequest(
                id = 3,
                draft = false,
                number = 3,
                repositoryUrl = "${Constants.BASE_API_URL}/repos/appunite/Blocktrade",
                title = "[SIL-62/SIL-75] Provide new annotation for API instances",
                updatedAt = "2022-01-29T16:31:41Z",
            ),
        ),
    )
}
