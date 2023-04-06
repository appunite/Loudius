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

package com.appunite.loudius.ui.reviewers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.ui.components.LoudiusFullScreenError
import com.appunite.loudius.ui.components.LoudiusListIcon
import com.appunite.loudius.ui.components.LoudiusListItem
import com.appunite.loudius.ui.components.LoudiusLoadingIndicator
import com.appunite.loudius.ui.components.LoudiusOutlinedButton
import com.appunite.loudius.ui.components.LoudiusPlaceholderText
import com.appunite.loudius.ui.components.LoudiusText
import com.appunite.loudius.ui.components.LoudiusTextStyle
import com.appunite.loudius.ui.components.LoudiusTopAppBar
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.FAILURE
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.SUCCESS
import com.appunite.loudius.ui.theme.LoudiusTheme

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
        content = { padding ->
            when {
                isError -> LoudiusFullScreenError(
                    modifier = Modifier.padding(padding),
                    onButtonClick = { onAction(ReviewersAction.OnTryAgain) },
                )
                isLoading -> LoudiusLoadingIndicator(Modifier.padding(padding))
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
                index = index,
                onNotifyClick = onNotifyClick,
            )
        }
    }
}

@Composable
private fun ReviewerItem(
    reviewer: Reviewer,
    index: Int,
    onNotifyClick: (ReviewersAction) -> Unit,
) {
    LoudiusListItem(
        index = index,
        icon = { ReviewerAvatarView(it) },
        content = {
            Column(modifier = it) {
                IsReviewedHeadlineText(reviewer)
                ReviewerName(reviewer)
            }
        },
        action = {
            NotifyButtonOrLoadingIndicator(reviewer = reviewer, onNotifyClick = onNotifyClick)
        },
    )
}

@Composable
private fun NotifyButtonOrLoadingIndicator(
    reviewer: Reviewer,
    onNotifyClick: (ReviewersAction) -> Unit,
) {
    Box(contentAlignment = Center) {
        LoudiusOutlinedButton(
            text = stringResource(R.string.details_notify),
            onClick = { onNotifyClick(ReviewersAction.Notify(reviewer.login)) },
            modifier = Modifier.alpha(if (reviewer.isLoading) 0f else 1f),
        )
        if (reviewer.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ReviewerAvatarView(modifier: Modifier = Modifier) {
    LoudiusListIcon(
        painter = painterResource(id = R.drawable.person_outline_24px),
        contentDescription = stringResource(
            R.string.details_screen_user_image_description,
        ),
        modifier = modifier,
    )
}

@Composable
private fun IsReviewedHeadlineText(reviewer: Reviewer) {
    LoudiusText(
        text = resolveIsReviewedText(reviewer),
        style = if (reviewer.isReviewDone) LoudiusTextStyle.ListHeader else LoudiusTextStyle.ListHeaderWarning,
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
    LoudiusText(
        text = reviewer.login,
        style = LoudiusTextStyle.ListItem,
    )
}

@Composable
private fun EmptyListPlaceholder(padding: PaddingValues) {
    Box(modifier = Modifier.padding(padding)) {
        LoudiusPlaceholderText(
            textId = R.string.you_dont_have_any_reviewers,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewerViewPreview() {
    LoudiusTheme {
        ReviewerItem(
            index = 0,
            reviewer = Reviewer(1, "Kezc", true, 12, 12),
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
