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
    onNavigateToLogin: () -> Unit,
) {
    val state = viewModel.state
    val code = intent.data?.getQueryParameter("code")
    val rememberedCode = rememberUpdatedState(newValue = code)
    LaunchedEffect(key1 = rememberedCode) {
        rememberedCode.value?.let {
            viewModel.setCodeAndGetAccessToken(it)
        }
    }
    LaunchedEffect(key1 = state.navigateTo) {
        when (state.navigateTo) {
            LoadingScreenNavigation.NavigateToLogin -> {
                onNavigateToLogin()
                viewModel.onAction(LoadingAction.OnNavigate)
            }
            LoadingScreenNavigation.NavigateToPullRequests -> {
                onNavigateToPullRequest()
                viewModel.onAction(LoadingAction.OnNavigate)
            }
            null -> {}
        }
    }
    LoadingScreenStateless(errorScreenType = state.errorScreenType) {
        viewModel.onAction(LoadingAction.OnTryAgainClick)
    }
}

@Composable
fun LoadingScreenStateless(
    errorScreenType: LoadingErrorType?,
    onTryAgainClick: () -> Unit,
) {
    when (errorScreenType) {
        LoadingErrorType.GENERIC_ERROR -> ShowLoudiusErrorScreen(onTryAgainClick)
        LoadingErrorType.LOGIN_ERROR -> ShowLoudiusLoginErrorScreen(onTryAgainClick)
        else -> LoudiusLoadingIndicator()
    }
}

@Composable
private fun ShowLoudiusLoginErrorScreen(
    onTryAgainClick: () -> Unit,
) {
    LoudiusErrorScreen(
        errorText = stringResource(id = R.string.error_login_text),
        buttonText = stringResource(id = R.string.go_to_login),
    ) {
        onTryAgainClick()
    }
}

@Composable
private fun ShowLoudiusErrorScreen(
    onTryAgainClick: () -> Unit,
) {
    LoudiusErrorScreen(
        onButtonClick = { onTryAgainClick() },
    )
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoudiusGenericErrorScreenPreview() {
    LoudiusTheme {
        LoadingScreenStateless(errorScreenType = LoadingErrorType.GENERIC_ERROR) {}
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoudiusLoginErrorScreenPreview() {
    LoudiusTheme {
        LoadingScreenStateless(errorScreenType = LoadingErrorType.LOGIN_ERROR) {}
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoadingIndicatorScreenPreview() {
    LoudiusTheme {
        LoadingScreenStateless(errorScreenType = null) {}
    }
}
