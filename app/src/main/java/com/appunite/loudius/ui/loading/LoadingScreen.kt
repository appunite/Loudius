package com.appunite.loudius.ui.loading

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.ui.components.LoudiusErrorScreen
import com.appunite.loudius.ui.components.LoudiusLoadingIndicator
import com.appunite.loudius.ui.theme.LoudiusTheme

@Composable
fun LoadingScreen(
    intent: Intent,
    viewModel: LoadingViewModel = hiltViewModel(),
    onNavigateToPullRequest: () -> Unit,
) {
    val state = viewModel.state
    val code = intent.data?.getQueryParameter("code")
    val rememberedCode = rememberUpdatedState(newValue = code)
    LaunchedEffect(key1 = rememberedCode) {
        rememberedCode.value?.let {
            viewModel.setCodeAndGetAccessToken(it)
        }
    }
    LaunchedEffect(key1 = state.navigateToPullRequests) {
        state.navigateToPullRequests?.let {
            onNavigateToPullRequest()
            viewModel.onAction(LoadingAction.OnNavigateToPullRequests)
        }
    }
    ResolveScreenToShow(showErrorScreen = state.showErrorScreen) {
        viewModel.onAction(LoadingAction.OnTryAgainClick)
    }
}

@Composable
fun ResolveScreenToShow(
    showErrorScreen: Boolean,
    onTryAgainClick: () -> Unit
) {
    if (showErrorScreen) {
        ShowLoudiusErrorScreen {
            onTryAgainClick()
        }
    } else {
        LoudiusLoadingIndicator()
    }
}

@Composable
private fun ShowLoudiusErrorScreen(
    onTryAgainClick: () -> Unit,
) {
    LoudiusErrorScreen(
        errorText = stringResource(id = R.string.error_dialog_text),
        buttonText = stringResource(id = R.string.try_again),
    ) {
        onTryAgainClick()
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoudiusErrorScreenPreview() {
    LoudiusTheme {
        ResolveScreenToShow(showErrorScreen = true) {}
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoadingIndicatorScreenPreview() {
    LoudiusTheme {
        ResolveScreenToShow(showErrorScreen = false) {}
    }
}
