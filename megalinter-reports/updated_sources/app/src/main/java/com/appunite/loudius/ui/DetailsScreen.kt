package com.appunite.loudius.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appunite.loudius.R
import com.appunite.loudius.domain.model.Reviewer
import com.appunite.loudius.ui.components.LoudiusTopAppBar
import com.appunite.loudius.ui.theme.LoudiusTheme
import com.appunite.loudius.ui.utils.bottomBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsScreenStateless(topBarTitle: String, reviewers: List<Reviewer>) {
    Scaffold(
        topBar = { LoudiusTopAppBar(onClickBackArrow = {}, title = topBarTitle) },
        content = { padding ->
            DetailsScreenContent(reviewers, modifier = Modifier.padding(padding))
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    )
}

@Composable
private fun DetailsScreenContent(reviewers: List<Reviewer>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
    ) {
        itemsIndexed(reviewers) { index, reviewer ->
            ReviewerView(
                reviewer = reviewer,
                backgroundColor = resolveReviewerBackgroundColor(index),
                onNotifyClick = {},
            )
        }
    }
}

@Composable
private fun resolveReviewerBackgroundColor(index: Int) =
    if (index % 2 == 0) MaterialTheme.colorScheme.onSurface.copy(0.08f) else MaterialTheme.colorScheme.surface

@Composable
private fun ReviewerView(reviewer: Reviewer, backgroundColor: Color, onNotifyClick: () -> Unit) {
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
        NotifyButton(onNotifyClick, Modifier.align(CenterVertically))
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
        text = reviewer.name,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun NotifyButton(onNotifyClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onNotifyClick, modifier = modifier) {
        Text(
            text = stringResource(R.string.details_notify),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
private fun ReviewerViewPreview() {
    LoudiusTheme {
        ReviewerView(
            reviewer = Reviewer("Kezc", true, 12, 12),
            backgroundColor = MaterialTheme.colorScheme.surface,
        ) {}
    }
}

@Preview
@Composable
fun DetailsScreenPreview() {
    val reviewer1 = Reviewer("Kezc", true, 24, 12)
    val reviewer2 = Reviewer("Krzysiudan", false, 24, 0)
    val reviewer3 = Reviewer("Weronika", false, 24, 0)
    val reviewer4 = Reviewer("Jacek", false, 24, 0)
    val reviewers = listOf(reviewer1, reviewer2, reviewer3, reviewer4)
    LoudiusTheme {
        DetailsScreenStateless(topBarTitle = "Pull request #1", reviewers = reviewers)
    }
}
