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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.BuildConfig
import com.appunite.loudius.analytics.EventTracker
import com.appunite.loudius.analytics.events.AuthenticatingEvents
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.common.Screen
import com.appunite.loudius.domain.repository.AuthRepository
import com.appunite.loudius.network.datasource.BadVerificationCodeException
import kotlinx.coroutines.launch

sealed class AuthenticatingAction {

    object OnNavigate : AuthenticatingAction()

    object OnTryAgainClick : AuthenticatingAction()
}

enum class LoadingErrorType {
    LOGIN_ERROR,
    GENERIC_ERROR
}

data class AuthenticatingState(
    val navigateTo: AuthenticatingScreenNavigation? = null,
    val errorScreenType: LoadingErrorType? = null
)

sealed class AuthenticatingScreenNavigation {
    object NavigateToPullRequests : AuthenticatingScreenNavigation()
    object NavigateToLogin : AuthenticatingScreenNavigation()
}

class AuthenticatingViewModel(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
    private val eventTracker: EventTracker
) : ViewModel() {

    private val code = Screen.Authenticating.getCode(savedStateHandle)
    var state by mutableStateOf(AuthenticatingState())
        private set

    init {
        getAccessToken()
    }

    fun onAction(action: AuthenticatingAction) = when (action) {
        is AuthenticatingAction.OnTryAgainClick -> onTryAgain()
        is AuthenticatingAction.OnNavigate -> onNavigate()
    }

    private fun onTryAgain() {
        if (state.errorScreenType == LoadingErrorType.LOGIN_ERROR) {
            state = state.copy(navigateTo = AuthenticatingScreenNavigation.NavigateToLogin)
        } else {
            state = state.copy(errorScreenType = null)
            getAccessToken()
        }
    }

    private fun onNavigate() {
        state = state.copy(navigateTo = null)
    }

    private fun getAccessToken() {
        eventTracker.trackEvent(AuthenticatingEvents.AuthenticationStarted)
        eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenStarted)
        code.fold(onSuccess = { code ->
            viewModelScope.launch {
                authRepository.fetchAccessToken(
                    clientId = CLIENT_ID,
                    clientSecret = BuildConfig.LOUDIUS_CLIENT_SECRET,
                    code = code
                ).onSuccess {
                    state = state.copy(
                        navigateTo = AuthenticatingScreenNavigation.NavigateToPullRequests
                    )
                    eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenFinishedSuccess)
                    eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedSuccess)
                }.onFailure {
                    state = state.copy(errorScreenType = resolveErrorType(it))
                    eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenFinishedFailure(it.message ?: "Unrecognised error."))
                    eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedFailure(it.message ?: "Unrecognised error."))
                }
            }
        }, onFailure = {
                state = state.copy(errorScreenType = LoadingErrorType.LOGIN_ERROR)
                eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedFailure(it.message ?: "Unrecognised error."))
            })
    }

    private fun resolveErrorType(it: Throwable) = when (it) {
        is BadVerificationCodeException -> LoadingErrorType.LOGIN_ERROR
        else -> LoadingErrorType.GENERIC_ERROR
    }

    fun trackScreenOpened() {
        eventTracker.trackEvent(AuthenticatingEvents.ScreenOpened)
    }
}
