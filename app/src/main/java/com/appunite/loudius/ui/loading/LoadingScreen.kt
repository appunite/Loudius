package com.appunite.loudius.ui.loading

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.ui.components.LoudiusErrorScreen

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
        ShowLoadingIndicator(code = code)
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
private fun ShowLoadingIndicator(code: String?) {
    // TODO add loading indicator
    Column {
        Text(text = code ?: "code is already consumed")
    }
}
