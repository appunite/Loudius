package com.appunite.loudius.ui.reviewers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.ui.components.LoudiusErrorScreen
import com.appunite.loudius.ui.components.LoudiusLoadingIndicator
import com.appunite.loudius.ui.components.LoudiusPlaceholderText
import com.appunite.loudius.ui.components.LoudiusTopAppBar
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.FAILURE
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.SUCCESS
import com.appunite.loudius.ui.theme.LoudiusTheme
import com.appunite.loudius.ui.utils.bottomBorder

@Composable
fun ReviewersScreen(
    viewModel: ReviewersViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }

    ReviewersScreenStateless(
        pullRequestNumber = state.pullRequestNumber,
        reviewers = state.reviewers,
        isLoading = state.isLoading,
        isError = state.isError,
        onClickBackArrow = navigateBack,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
    if (state.snackbarTypeShown != null) {
        SnackbarLaunchedEffect(
            snackbarTypeShown = state.snackbarTypeShown,
            snackbarHostState = snackbarHostState,
            onSnackbarDismiss = viewModel::onAction,
        )
    }
}

@Composable
private fun SnackbarLaunchedEffect(
    snackbarTypeShown: ReviewersSnackbarType,
    snackbarHostState: SnackbarHostState,
    onSnackbarDismiss: (ReviewersAction) -> Unit,
) {
    val snackbarMessage = resolveSnackbarMessage(snackbarTypeShown)

    LaunchedEffect(snackbarTypeShown) {
        val result = snackbarHostState.showSnackbar(message = snackbarMessage)
        if (result == SnackbarResult.Dismissed) {
            onSnackbarDismiss(ReviewersAction.OnSnackbarDismiss)
        }
    }
}

@Composable
private fun resolveSnackbarMessage(snackbarTypeShown: ReviewersSnackbarType) =
    when (snackbarTypeShown) {
        SUCCESS -> stringResource(id = R.string.reviewers_snackbar_success)
        FAILURE -> stringResource(id = R.string.reviewers_snackbar_failure)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewersScreenStateless(
    pullRequestNumber: String,
    reviewers: List<Reviewer>,
    isLoading: Boolean,
    isError: Boolean,
    onClickBackArrow: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onAction: (ReviewersAction) -> Unit,
) {
    Scaffold(
        topBar = {
            LoudiusTopAppBar(
                onClickBackArrow = onClickBackArrow,
                title = stringResource(id = R.string.details_title, pullRequestNumber),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        content = { padding ->
            when {
                isError -> LoudiusErrorScreen(onButtonClick = { onAction(ReviewersAction.OnTryAgain) })
                isLoading -> LoudiusLoadingIndicator()
                reviewers.isEmpty() -> EmptyListPlaceholder(padding)
                else -> ReviewersScreenContent(
                    reviewers = reviewers,
                    modifier = Modifier.padding(padding),
                    onNotifyClick = onAction,
                )
            }
        },
    )
}

@Composable
private fun ReviewersScreenContent(
    reviewers: List<Reviewer>,
    modifier: Modifier,
    onNotifyClick: (ReviewersAction) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
    ) {
        itemsIndexed(reviewers) { index, reviewer ->
            ReviewerItem(
                reviewer = reviewer,
                backgroundColor = resolveReviewerBackgroundColor(index),
                onNotifyClick = onNotifyClick,
            )
        }
    }
}

@Composable
private fun resolveReviewerBackgroundColor(index: Int) =
    if (index % 2 == 0) MaterialTheme.colorScheme.onSurface.copy(0.08f) else MaterialTheme.colorScheme.surface

@Composable
private fun ReviewerItem(
    reviewer: Reviewer,
    backgroundColor: Color,
    onNotifyClick: (ReviewersAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .bottomBorder(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(16.dp),
    ) {
        ReviewerAvatarView(Modifier.align(CenterVertically))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
                .align(CenterVertically),
        ) {
            IsReviewedHeadlineText(reviewer)
            ReviewerName(reviewer)
        }
        NotifyButtonOrLoadingIndicator(reviewer = reviewer, onNotifyClick = onNotifyClick)
    }
}

@Composable
private fun NotifyButtonOrLoadingIndicator(
    reviewer: Reviewer,
    onNotifyClick: (ReviewersAction) -> Unit,
) {
    val buttonAlpha = if (reviewer.isLoading) 0f else 1f
    Box(contentAlignment = Center) {
        NotifyButton(
            modifier = Modifier.alpha(buttonAlpha),
        ) { onNotifyClick(ReviewersAction.Notify(reviewer.login)) }

        if (reviewer.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ReviewerAvatarView(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.person_outline_24px),
        contentDescription = stringResource(
            R.string.details_screen_user_image_description,
        ),
        modifier = modifier,
    )
}

@Composable
private fun IsReviewedHeadlineText(reviewer: Reviewer) {
    Text(
        text = resolveIsReviewedText(reviewer),
        style = MaterialTheme.typography.labelMedium,
        color = if (reviewer.isReviewDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error,
    )
}

@Composable
private fun resolveIsReviewedText(reviewer: Reviewer) = if (reviewer.isReviewDone) {
    stringResource(id = R.string.details_reviewed, reviewer.hoursFromReviewDone ?: 0)
} else {
    stringResource(id = R.string.details_not_reviewed, reviewer.hoursFromPRStart)
}

@Composable
private fun ReviewerName(reviewer: Reviewer) {
    Text(
        text = reviewer.login,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun NotifyButton(modifier: Modifier = Modifier, onNotifyClick: () -> Unit) {
    OutlinedButton(onClick = onNotifyClick, modifier = modifier) {
        Text(
            text = stringResource(R.string.details_notify),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun EmptyListPlaceholder(padding: PaddingValues) {
    LoudiusPlaceholderText(
        textId = R.string.you_dont_have_any_reviewers,
        padding = padding,
    )
}

@Preview
@Composable
private fun ReviewerViewPreview() {
    LoudiusTheme {
        ReviewerItem(
            reviewer = Reviewer(1, "Kezc", true, 12, 12),
            backgroundColor = MaterialTheme.colorScheme.surface,
        ) {}
    }
}

@Preview
@Composable
fun DetailsScreenPreview() {
    val reviewer1 = Reviewer(1, "Kezc", true, 24, 12)
    val reviewer2 = Reviewer(2, "Krzysiudan", false, 24, 0)
    val reviewer3 = Reviewer(3, "Weronika", false, 24, 0, true)
    val reviewer4 = Reviewer(4, "Jacek", false, 24, 0)
    val reviewers = listOf(reviewer1, reviewer2, reviewer3, reviewer4)
    LoudiusTheme {
        ReviewersScreenStateless(
            pullRequestNumber = "1",
            reviewers = reviewers,
            isError = false,
            isLoading = false,
            onClickBackArrow = {},
            snackbarHostState = SnackbarHostState(),
            onAction = {},
        )
    }
}
@Preview
@Composable
fun DetailsScreenNoReviewsPreview() {
    LoudiusTheme {
        ReviewersScreenStateless(
            pullRequestNumber = "1",
            reviewers = emptyList(),
            isError = false,
            isLoading = false,
            onClickBackArrow = {},
            snackbarHostState = SnackbarHostState(),
            onAction = {},
        )
    }
}
