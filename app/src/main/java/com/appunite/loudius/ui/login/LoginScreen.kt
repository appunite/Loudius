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

package com.appunite.loudius.ui.login

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.appunite.loudius.R
import com.appunite.loudius.common.Constants.AUTH_API_URL
import com.appunite.loudius.common.Constants.AUTH_PATH
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.common.Constants.NAME_PARAM_CLIENT_ID
import com.appunite.loudius.common.Constants.SCOPE_PARAM
import com.appunite.loudius.ui.components.LoudiusDialog
import com.appunite.loudius.ui.components.LoudiusOutlinedButton
import com.appunite.loudius.ui.components.LoudiusOutlinedButtonIcon
import com.appunite.loudius.ui.components.LoudiusOutlinedButtonStyle
import com.appunite.loudius.ui.components.LoudiusText
import com.appunite.loudius.ui.components.LoudiusTextStyle

@Composable
fun LoginScreen(
    viewModel: LoginScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val navigateTo = viewModel.state.navigateTo
    LaunchedEffect(navigateTo) {
        when (navigateTo) {
            LoginNavigateTo.OpenGithubAuth -> {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(buildAuthorizationUrl()),
                    ),
                )
                viewModel.onAction(LoginAction.ClearNavigation)
            }
            LoginNavigateTo.OpenXiaomiPermissionManager -> {
                context.startActivity(GithubHelper.xiaomiPermissionManagerForGithub())
            }
            null -> Unit
        }
    }
    LoginScreenStateless(
        state = viewModel.state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun LoginScreenStateless(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginImage()
        LoudiusOutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onAction(LoginAction.ClickLogIn)
            },
            text = stringResource(id = R.string.login_screen_login),
            style = LoudiusOutlinedButtonStyle.Large,
            icon = {
                LoudiusOutlinedButtonIcon(
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = stringResource(R.string.github_icon),
                )
            },

        )
        if (state.showXiaomiPermissionDialog) {
            LoudiusDialog(
                onDismissRequest = { onAction(LoginAction.XiaomiPermissionDialogDismiss) },
                title = stringResource(R.string.login_screen_xiaomi_dialog_title),
                text = {
                    LoudiusText(
                        style = LoudiusTextStyle.ScreenContent,
                        text = stringResource(R.string.login_screen_xiaomi_dialog_text),
                    )
                },
                confirmButton = {
                    LoudiusOutlinedButton(text = stringResource(R.string.login_screen_xiaomi_dialog_grant_permission)) {
                        onAction(LoginAction.XiaomiPermissionDialogGrantPermission)
                    }
                },
                dismissButton = {
                    LoudiusOutlinedButton(text = stringResource(R.string.login_screen_xiaomi_dialog_cancel)) {
                        onAction(LoginAction.XiaomiPermissionDialogDismiss)
                    }
                },
            )
        }
    }
}

@Composable
fun LoginImage() {
    Image(
        painter = painterResource(id = R.drawable.loudius_logo),
        contentDescription = stringResource(
            R.string.login_screen_loudius_logo_content_description,
        ),
    )
}

private fun buildAuthorizationUrl() = AUTH_API_URL + AUTH_PATH + NAME_PARAM_CLIENT_ID + CLIENT_ID + SCOPE_PARAM

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreenStateless(
            state = LoginState(showXiaomiPermissionDialog = false, navigateTo = null),
            onAction = {},
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoginScreenPreviewWithDialog() {
    MaterialTheme {
        LoginScreenStateless(
            state = LoginState(showXiaomiPermissionDialog = true, navigateTo = null),
            onAction = {},
        )
    }
}
