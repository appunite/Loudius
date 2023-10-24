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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

sealed class LoginAction {
    object ClearNavigation : LoginAction()
    object ClickLogIn : LoginAction()
    object ClickBrowseComponents : LoginAction()
    object XiaomiPermissionDialogDismiss : LoginAction()
    object XiaomiPermissionDialogGrantPermission : LoginAction()
    object XiaomiPermissionDialogAlreadyGrantedPermission : LoginAction()
}

sealed class LoginNavigateTo {
    object OpenXiaomiPermissionManager : LoginNavigateTo()
    object OpenGithubAuth : LoginNavigateTo()
    object OpenComponentsBrowser : LoginNavigateTo()
}

data class LoginState(
    val showXiaomiPermissionDialog: Boolean = false,
    val navigateTo: LoginNavigateTo? = null
)

class LoginScreenViewModel(
    private val githubHelper: GithubHelper
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.ClearNavigation -> {
                state = state.copy(navigateTo = null)
            }

            LoginAction.ClickLogIn -> {
                if (githubHelper.shouldAskForXiaomiIntent()) {
                    state = state.copy(
                        showXiaomiPermissionDialog = true
                    )
                } else {
                    state = state.copy(navigateTo = LoginNavigateTo.OpenGithubAuth)
                }
            }

            LoginAction.XiaomiPermissionDialogDismiss -> {
                state = state.copy(showXiaomiPermissionDialog = false)
            }

            LoginAction.XiaomiPermissionDialogGrantPermission -> {
                state = state.copy(navigateTo = LoginNavigateTo.OpenXiaomiPermissionManager)
            }

            LoginAction.XiaomiPermissionDialogAlreadyGrantedPermission -> {
                state = state.copy(
                    showXiaomiPermissionDialog = false,
                    navigateTo = LoginNavigateTo.OpenGithubAuth
                )
            }

            LoginAction.ClickBrowseComponents -> {
                state = state.copy(navigateTo = LoginNavigateTo.OpenComponentsBrowser)
            }
        }
    }
}
