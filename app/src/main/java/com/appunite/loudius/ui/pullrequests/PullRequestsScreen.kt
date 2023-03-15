@file:OptIn(ExperimentalMaterial3Api::class)

package com.appunite.loudius.ui.pullrequests

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.common.Constants
import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.ui.components.LoudiusErrorScreen
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
    Scaffold(topBar = {
        LoudiusTopAppBar(title = stringResource(R.string.app_name))
    }, content = { padding ->
        when {
            isError -> LoudiusErrorScreen(
                errorText = stringResource(id = R.string.error_dialog_text),
                buttonText = stringResource(R.string.try_again_text),
                onButtonClick = { onAction(PulLRequestsAction.RetryClick) },
            )
            isLoading -> LoadingIndicator(modifier = Modifier.padding(padding))
            else -> PullRequestsList(
                pullRequests = pullRequests,
                modifier = Modifier.padding(padding),
                onItemClick = onAction,
            )
        }
    })
}

@Composable
private fun LoadingIndicator(modifier: Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator()
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
            val isIndexEven = index % 2 == 0
            PullRequestItem(
                data = item,
                darkBackground = isIndexEven,
                onClick = onItemClick,
            )
        }
    }
}

@Composable
private fun PullRequestItem(
    data: PullRequest,
    darkBackground: Boolean,
    onClick: (PulLRequestsAction) -> Unit,
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
            .clickable { onClick(PulLRequestsAction.ItemClick(data.id)) },
    ) {
        PullRequestIcon()
        RepoDetails(pullRequestTitle = data.title, repositoryName = data.fullRepositoryName)
    }
    Divider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun PullRequestIcon() {
    Image(
        painter = painterResource(id = R.drawable.ic_share),
        contentDescription = null,
        modifier = Modifier
            .padding(start = 18.dp, top = 10.dp)
            .size(width = 18.dp, height = 20.dp),
    )
}

@Composable
private fun RepoDetails(pullRequestTitle: String, repositoryName: String) {
    Column(Modifier.padding(start = 18.dp, top = 8.dp, bottom = 8.dp)) {
        Text(text = pullRequestTitle, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = repositoryName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
