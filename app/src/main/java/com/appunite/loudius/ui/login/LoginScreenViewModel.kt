package com.appunite.loudius.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class LoginAction {
    object ClearNavigation : LoginAction()
    object ClickLogIn : LoginAction()
    object XiaomiPermissionDialogDismiss : LoginAction()
    object XiaomiPermissionDialogGrantPermission : LoginAction()
    object XiaomiPermissionDialogAlreadyGrantedPermission : LoginAction()
}

sealed class LoginNavigateTo {
    object OpenXiaomiPermissionManager : LoginNavigateTo()
    object OpenGithubAuth : LoginNavigateTo()
}

data class LoginState(
    val showXiaomiPermissionDialog: Boolean,
    val navigateTo: LoginNavigateTo?,
)

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val githubHelper: GithubHelper,
) : ViewModel() {

    var state by mutableStateOf(LoginState(showXiaomiPermissionDialog = false, navigateTo = null))
        private set

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.ClearNavigation -> {
                state = state.copy(navigateTo = null)
            }

            LoginAction.ClickLogIn -> {
                if (githubHelper.shouldAskForXiaomiIntent()) {
                    state = state.copy(
                        showXiaomiPermissionDialog = true,
                    )
                } else {
                    state = state.copy(
                        navigateTo = LoginNavigateTo.OpenGithubAuth,
                    )
                }
            }

            LoginAction.XiaomiPermissionDialogDismiss -> {
                state = state.copy(showXiaomiPermissionDialog = false)
            }

            LoginAction.XiaomiPermissionDialogGrantPermission -> {
                state = state.copy(
                    navigateTo = LoginNavigateTo.OpenXiaomiPermissionManager,
                )
            }

            LoginAction.XiaomiPermissionDialogAlreadyGrantedPermission -> {
                state = state.copy(
                    showXiaomiPermissionDialog = false,
                    navigateTo = LoginNavigateTo.OpenGithubAuth,
                )
            }
        }
    }
}
