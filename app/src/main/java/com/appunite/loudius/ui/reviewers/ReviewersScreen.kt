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

@file:OptIn(ExperimentalMaterialApi::class)

package com.appunite.loudius.ui.reviewers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.appunite.loudius.R
import com.appunite.loudius.components.components.LoudiusFullScreenError
import com.appunite.loudius.components.components.LoudiusListIcon
import com.appunite.loudius.components.components.LoudiusListItem
import com.appunite.loudius.components.components.LoudiusLoadingIndicator
import com.appunite.loudius.components.components.LoudiusOutlinedButton
import com.appunite.loudius.components.components.LoudiusPlaceholderText
import com.appunite.loudius.components.components.LoudiusPullToRefreshBox
import com.appunite.loudius.components.components.LoudiusText
import com.appunite.loudius.components.components.LoudiusTextStyle
import com.appunite.loudius.components.components.LoudiusTopAppBar
import com.appunite.loudius.components.theme.LoudiusTheme
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.FAILURE
import com.appunite.loudius.ui.reviewers.ReviewersSnackbarType.SUCCESS
import com.appunite.loudius.components.R as componentsR

@Composable
fun reviewersScreen(
    viewModel: ReviewersViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val viewModel: ReviewersViewModel = hiltViewModel()
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshing by viewModel.isRefreshing.collectAsState()

    ReviewersScreenStateless(
        pullRequestNumber = state.pullRequestNumber,
        data = state.data,
        refreshing = refreshing,
        onRefresh = viewModel::refreshData,
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
        SUCCESS -> stringResource(id = R.string.reviewers_screen_snackbar_success_message)
        FAILURE -> stringResource(id = R.string.reviewers_screen_snackbar_failure_message)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewersScreenStateless(
    pullRequestNumber: String,
    data: Data,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onClickBackArrow: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onAction: (ReviewersAction) -> Unit,
) {
    Scaffold(
        topBar = {
            LoudiusTopAppBar(
                onClickBackArrow = onClickBackArrow,
                title = stringResource(id = R.string.reviewers_screen_title, pullRequestNumber),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { padding ->
            when (data) {
                is Data.Error -> LoudiusFullScreenError(
                    modifier = Modifier.padding(padding),
                    onButtonClick = { onAction(ReviewersAction.OnTryAgain) },
                )

                is Data.Loading -> LoudiusLoadingIndicator(Modifier.padding(padding))
                is Data.Success -> ReviewersScreenContent(
                    data,
                    refreshing,
                    onRefresh,
                    padding,
                    onAction,
                )
            }
        },
    )
}

@Composable
private fun ReviewersScreenContent(
    data: Data.Success,
    refreshing: Boolean,
    onRefreshing: () -> Unit,
    padding: PaddingValues,
    onAction: (ReviewersAction) -> Unit,
) {
    if (data.reviewers.isNotEmpty()) {
        ReviewersList(
            data = data,
            pullRefreshState = rememberPullRefreshState(
                refreshing = refreshing,
                onRefresh = onRefreshing,
            ),
            modifier = Modifier.padding(padding),
            onNotifyClick = onAction,
            refreshing = refreshing,
        )
    } else {
        EmptyListPlaceholder(padding)
    }
}

@Composable
private fun ReviewersList(
    data: Data,
    pullRefreshState: PullRefreshState,
    modifier: Modifier,
    onNotifyClick: (ReviewersAction) -> Unit,
    refreshing: Boolean,
) {
    LoudiusPullToRefreshBox(
        pullRefreshState = pullRefreshState,
        refreshing = refreshing,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed((data as Data.Success).reviewers) { index, reviewer ->
                ReviewerItem(
                    reviewer = reviewer,
                    index = index,
                    onNotifyClick = onNotifyClick,
                )
            }
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
            text = stringResource(R.string.reviewers_screen_notify_button),
            onClick = { onNotifyClick(ReviewersAction.Notify(reviewer.login)) },
            modifier = Modifier.alpha(if (reviewer.isLoading) 0f else 1f),
        )
        if (reviewer.isLoading) {
            LoudiusLoadingIndicator(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ReviewerAvatarView(modifier: Modifier = Modifier) {
    LoudiusListIcon(
        painter = painterResource(id = componentsR.drawable.components_person_outline_24px),
        contentDescription = stringResource(
            R.string.reviewers_screen_user_image_content_description,
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
    stringResource(
        id = R.string.reviewers_screen_reviewed_message,
        reviewer.hoursFromReviewDone ?: 0,
    )
} else {
    stringResource(id = R.string.reviewers_screen_not_reviewed_message, reviewer.hoursFromPRStart)
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
            text = stringResource(R.string.reviewers_screen_you_dont_have_any_reviewers_message),
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

@Preview(showBackground = true)
@Composable
private fun ReviewerViewLoadingPreview() {
    LoudiusTheme {
        ReviewerItem(
            index = 0,
            reviewer = Reviewer(1, "Kezc", true, 12, 12, isLoading = true),
        ) {}
    }
}

private val successData = listOf(
    Reviewer(1, "Kezc", true, 24, 12),
    Reviewer(2, "Krzysiudan", false, 24, 0),
    Reviewer(3, "Weronika", false, 24, 0, true),
    Reviewer(4, "Jacek", false, 24, 0),
)

@Preview
@Composable
@ShowkaseComposable(skip = true)
fun DetailsScreenPreview() {
    LoudiusTheme {
        ReviewersScreenStateless(
            pullRequestNumber = "1",
            data = Data.Success(successData),
            onClickBackArrow = {},
            snackbarHostState = SnackbarHostState(),
            onAction = {},
            refreshing = false,
            onRefresh = {},
        )
    }
}

@Preview
@Composable
@ShowkaseComposable(skip = true)
fun DetailsScreenNoReviewsPreview() {
    LoudiusTheme {
        ReviewersScreenStateless(
            pullRequestNumber = "1",
            data = Data.Success(emptyList()),
            onClickBackArrow = {},
            snackbarHostState = SnackbarHostState(),
            onAction = {},
            refreshing = false,
            onRefresh = {},
        )
    }
}

@Preview
@Composable
@ShowkaseComposable(skip = true)
fun DetailsScreenRefreshingPreview() {
    LoudiusTheme {
        ReviewersScreenStateless(
            pullRequestNumber = "1",
            data = Data.Success(successData),
            onClickBackArrow = {},
            snackbarHostState = SnackbarHostState(),
            onAction = {},
            refreshing = true,
            onRefresh = {},
        )
    }
}
