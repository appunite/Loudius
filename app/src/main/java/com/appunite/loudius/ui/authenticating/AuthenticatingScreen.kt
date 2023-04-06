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

package com.appunite.loudius.ui.authenticating

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.ui.components.LoudiusFullScreenError
import com.appunite.loudius.ui.components.LoudiusLoadingIndicator
import com.appunite.loudius.ui.theme.LoudiusTheme

@Composable
fun AuthenticatingScreen(
    viewModel: AuthenticatingViewModel = hiltViewModel(),
    onNavigateToPullRequest: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val state = viewModel.state
    LaunchedEffect(key1 = state.navigateTo) {
        when (state.navigateTo) {
            AuthenticatingScreenNavigation.NavigateToLogin -> {
                onNavigateToLogin()
                viewModel.onAction(AuthenticatingAction.OnNavigate)
            }
            AuthenticatingScreenNavigation.NavigateToPullRequests -> {
                onNavigateToPullRequest()
                viewModel.onAction(AuthenticatingAction.OnNavigate)
            }
            null -> {}
        }
    }
    AuthenticatingScreenStateless(errorScreenType = state.errorScreenType) {
        viewModel.onAction(AuthenticatingAction.OnTryAgainClick)
    }
}

@Composable
fun AuthenticatingScreenStateless(
    errorScreenType: LoadingErrorType?,
    onTryAgainClick: () -> Unit,
) {
    when (errorScreenType) {
        LoadingErrorType.GENERIC_ERROR -> ShowLoudiusGenericErrorScreen(onTryAgainClick)
        LoadingErrorType.LOGIN_ERROR -> ShowLoudiusLoginErrorScreen(onTryAgainClick)
        else -> LoudiusLoadingIndicator()
    }
}

@Composable
private fun ShowLoudiusLoginErrorScreen(
    onTryAgainClick: () -> Unit,
) {
    LoudiusFullScreenError(
        errorText = stringResource(id = R.string.error_login_text),
        buttonText = stringResource(id = R.string.go_to_login),
        onButtonClick = onTryAgainClick,
    )
}

@Composable
private fun ShowLoudiusGenericErrorScreen(
    onTryAgainClick: () -> Unit,
) {
    LoudiusFullScreenError(onButtonClick = onTryAgainClick)
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoudiusGenericErrorScreenPreview() {
    LoudiusTheme {
        AuthenticatingScreenStateless(errorScreenType = LoadingErrorType.GENERIC_ERROR) {}
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoudiusLoginErrorScreenPreview() {
    LoudiusTheme {
        AuthenticatingScreenStateless(errorScreenType = LoadingErrorType.LOGIN_ERROR) {}
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoadingIndicatorScreenPreview() {
    LoudiusTheme {
        AuthenticatingScreenStateless(errorScreenType = null) {}
    }
}
