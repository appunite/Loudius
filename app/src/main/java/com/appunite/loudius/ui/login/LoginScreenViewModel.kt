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
import com.appunite.loudius.analytics.EventTracker
import com.appunite.loudius.analytics.events.LoginEvents
import kotlin.random.Random

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
    private val githubHelper: GithubHelper,
    private val eventTracker: EventTracker
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    init {
        for (i in 1..Random.nextInt(3, 12)) {
            eventTracker.trackEvent(LoginEvents.ScreenOpened)
        }
        for (i in 1..Random.nextInt(3, 12)) {
            eventTracker.trackEvent(LoginEvents.ClickLogIn)
        }
        for (i in 1..Random.nextInt(3, 12)) {
            eventTracker.trackEvent(LoginEvents.OpenGithubAuth)
        }
        for (i in 1..Random.nextInt(3, 12)) {
            eventTracker.trackEvent(LoginEvents.XiaomiPermissionDialogDismissed)
        }
        for (i in 1..Random.nextInt(3, 12)) {
            eventTracker.trackEvent(LoginEvents.XiaomiPermissionDialogPermissionGranted)
        }
        for (i in 1..Random.nextInt(3, 12)) {
            eventTracker.trackEvent(LoginEvents.XiaomiPermissionDialogPermissionAlreadyGranted)
        }
    }

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.ClearNavigation -> {
                state = state.copy(navigateTo = null)
            }

            LoginAction.ClickLogIn -> {
                eventTracker.trackEvent(LoginEvents.ClickLogIn)
                if (githubHelper.shouldAskForXiaomiIntent()) {
                    state = state.copy(
                        showXiaomiPermissionDialog = true
                    )
                } else {
                    eventTracker.trackEvent(LoginEvents.OpenGithubAuth)
                    state = state.copy(navigateTo = LoginNavigateTo.OpenGithubAuth)
                }
            }

            LoginAction.XiaomiPermissionDialogDismiss -> {
                eventTracker.trackEvent(LoginEvents.XiaomiPermissionDialogDismissed)
                state = state.copy(showXiaomiPermissionDialog = false)
            }

            LoginAction.XiaomiPermissionDialogGrantPermission -> {
                eventTracker.trackEvent(LoginEvents.XiaomiPermissionDialogPermissionGranted)
                state = state.copy(navigateTo = LoginNavigateTo.OpenXiaomiPermissionManager)
            }

            LoginAction.XiaomiPermissionDialogAlreadyGrantedPermission -> {
                eventTracker.trackEvent(LoginEvents.XiaomiPermissionDialogPermissionAlreadyGranted)
                eventTracker.trackEvent(LoginEvents.OpenGithubAuth)
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

    fun trackScreenOpened() {
        eventTracker.trackEvent(LoginEvents.ScreenOpened)
    }
}
