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
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.appunite.loudius.R
import com.appunite.loudius.components.components.LoudiusFullScreenError
import com.appunite.loudius.components.components.LoudiusLoadingIndicator
import com.appunite.loudius.components.theme.LoudiusTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthenticatingScreen(
    viewModel: AuthenticatingViewModel = koinViewModel(),
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
    navigateToLogin: () -> Unit,
) {
    LoudiusFullScreenError(
        errorText = stringResource(id = R.string.authenticating_screen_error_screen_error_message),
        buttonText = stringResource(id = R.string.authenticating_screen_error_screen_login_button),
        onButtonClick = navigateToLogin,
    )
}

@Composable
private fun ShowLoudiusGenericErrorScreen(
    onTryAgainClick: () -> Unit,
) {
    LoudiusFullScreenError(onButtonClick = onTryAgainClick)
}

@Preview(showSystemUi = true, group = "Full screen")
@Composable
fun ShowLoudiusGenericErrorScreenPreview() {
    LoudiusTheme {
        AuthenticatingScreenStateless(errorScreenType = LoadingErrorType.GENERIC_ERROR) {}
    }
}

@Preview(showSystemUi = true, group = "Full screen")
@Composable
@ShowkaseComposable(skip = true)
fun ShowLoudiusLoginErrorScreenPreview() {
    LoudiusTheme {
        AuthenticatingScreenStateless(errorScreenType = LoadingErrorType.LOGIN_ERROR) {}
    }
}

@Preview(showSystemUi = true, group = "Full screen")
@Composable
fun ShowLoadingIndicatorScreenPreview() {
    LoudiusTheme {
        AuthenticatingScreenStateless(errorScreenType = null) {}
    }
}
