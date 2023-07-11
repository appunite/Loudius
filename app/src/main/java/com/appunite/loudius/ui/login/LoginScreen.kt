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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.android.showkase.models.Showkase
import com.appunite.loudius.BuildConfig
import com.appunite.loudius.R
import com.appunite.loudius.common.Constants.AUTHORIZATION_URL
import com.appunite.loudius.components.components.LoudiusDialog
import com.appunite.loudius.components.components.LoudiusOutlinedButton
import com.appunite.loudius.components.components.LoudiusOutlinedButtonIcon
import com.appunite.loudius.components.components.LoudiusOutlinedButtonStyle
import com.appunite.loudius.components.components.LoudiusText
import com.appunite.loudius.components.components.LoudiusTextStyle
import com.appunite.loudius.components.getBrowserIntent
import com.appunite.loudius.components.R as componentsR

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
                    Intent(Intent.ACTION_VIEW, Uri.parse(AUTHORIZATION_URL)),
                )
                viewModel.onAction(LoginAction.ClearNavigation)
            }

            LoginNavigateTo.OpenXiaomiPermissionManager -> {
                context.startActivity(GithubHelper.xiaomiPermissionManagerForGithub())
            }

            LoginNavigateTo.OpenComponentsBrowser -> {
                context.startActivity(Showkase.getBrowserIntent(context))
                viewModel.onAction(LoginAction.ClearNavigation)
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
    if (BuildConfig.DEBUG) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            BrowseComponentIcon(onAction)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginImage()
        LoudiusOutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 46.dp),
            onClick = {
                onAction(LoginAction.ClickLogIn)
            },
            text = stringResource(id = R.string.login_screen_login_button),
            style = LoudiusOutlinedButtonStyle.Large,
            icon = {
                LoudiusOutlinedButtonIcon(
                    contentDescription = stringResource(R.string.login_screen_github_icon_content_description),
                    painter = painterResource(id = componentsR.drawable.components_ic_github),
                )
            },
        )
        if (state.showXiaomiPermissionDialog) {
            XiaomiPermissionDialog(onAction)
        }
    }

}

@Composable
private fun BrowseComponentIcon(onClick: (LoginAction) -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_components_browser),
        contentDescription = stringResource(
            id = R.string.login_screen_loudius_browser_components_icon_description
        ),
        modifier = Modifier
            .padding(16.dp)
            .size(24.dp)
            .clickable { onClick(LoginAction.ClickBrowseComponents) }
    )
}

@Composable
private fun XiaomiPermissionDialog(onAction: (LoginAction) -> Unit) {
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
            LoudiusOutlinedButton(text = stringResource(R.string.login_screen_xiaomi_dialog_already_granted)) {
                onAction(LoginAction.XiaomiPermissionDialogAlreadyGrantedPermission)
            }
        },
    )
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
