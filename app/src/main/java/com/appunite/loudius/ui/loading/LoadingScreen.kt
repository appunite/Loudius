package com.appunite.loudius.ui.loading

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
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
    LoadingScreenStateless(showErrorScreen = state.showErrorScreen) {
        viewModel.onAction(LoadingAction.OnTryAgainClick)
    }
}

@Composable
fun LoadingScreenStateless(
    showErrorScreen: Boolean,
    onTryAgainClick: () -> Unit,
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
        onButtonClick = { onTryAgainClick() }
    )
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoudiusErrorScreenPreview() {
    LoudiusTheme {
        LoadingScreenStateless(showErrorScreen = true) {}
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoadingIndicatorScreenPreview() {
    LoudiusTheme {
        LoadingScreenStateless(showErrorScreen = false) {}
    }
}
