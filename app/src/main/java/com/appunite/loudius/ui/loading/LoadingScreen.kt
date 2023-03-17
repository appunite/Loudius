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

    if (state.showErrorScreen) {
        ShowLoudiusErrorScreen {
            viewModel.onAction(LoadingAction.OnTryAgainClick)
        }
    } else {
        ShowLoadingIndicator()
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

@Composable
private fun ShowLoadingIndicator() {
    LoudiusLoadingIndicator()
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoudiusErrorScreenPreview() {
    LoudiusTheme {
        ShowLoudiusErrorScreen {}
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoudiusLoadingIndicatorPreview() {
    LoudiusTheme {
        ShowLoadingIndicator()
    }
}
