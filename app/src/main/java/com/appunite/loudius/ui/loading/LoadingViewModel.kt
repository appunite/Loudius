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

package com.appunite.loudius.ui.loading

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController.Companion.KEY_DEEP_LINK_INTENT
import com.appunite.loudius.BuildConfig
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.domain.repository.AuthRepository
import com.appunite.loudius.network.utils.WebException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoadingAction {

    object OnNavigate : LoadingAction()

    object OnTryAgainClick : LoadingAction()
}

enum class LoadingErrorType {
    LOGIN_ERROR,
    GENERIC_ERROR,
}

data class LoadingState(
    // question: why do we need this token in the state?
    val accessToken: String? = null,
    // suggestion: we can remove this code from the state
    val code: String? = null,
    // question: I wonder if this is relly beneficial having actions in state. We need to manage
    // them separately and also remember to clear them. I wonder what it gives us.
    val navigateTo: LoadingScreenNavigation? = null,
    val errorScreenType: LoadingErrorType? = null,
)

sealed class LoadingScreenNavigation {
    object NavigateToPullRequests : LoadingScreenNavigation()
    object NavigateToLogin : LoadingScreenNavigation()
}

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var state by mutableStateOf(LoadingState())
        private set

    init {
        // suggestion: much cleaner solution for retrieving code from intent than doing this is UI:
        val intent : Intent? = savedStateHandle[KEY_DEEP_LINK_INTENT]
        val code = intent?.data?.getQueryParameter("code")
        if (code != null) {
            setCodeAndGetAccessToken(code)
        }
        // morover we can move part of the logic to Screen.Xyz class
    }

    private fun setCodeAndGetAccessToken(code: String) {
        state = state.copy(code = code)
        getAccessToken(code)
    }

    // question: I wonder, what gives as creating onAction, then just calling `onTryAgain()` and `onNavigate()` methods?
    fun onAction(action: LoadingAction) = when (action) {
        is LoadingAction.OnTryAgainClick -> onTryAgain()
        is LoadingAction.OnNavigate -> onNavigate()
    }

    private fun onTryAgain() {
        if (state.errorScreenType == LoadingErrorType.LOGIN_ERROR) {
            state = state.copy(navigateTo = LoadingScreenNavigation.NavigateToLogin)
        } else {
            state = state.copy(errorScreenType = null)
            state.code?.let {
                getAccessToken(it)
            }
        }
    }

    private fun onNavigate() {
        state = state.copy(navigateTo = null)
    }

    private fun getAccessToken(code: String) {
        viewModelScope.launch {
            authRepository.fetchAccessToken(
                clientId = CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                code = code,
            ).onSuccess { token ->
                state = state.copy(
                    accessToken = token,
                    navigateTo = LoadingScreenNavigation.NavigateToPullRequests,
                )
            }.onFailure {
                state = state.copy(errorScreenType = resolveErrorType(it))
            }
        }
    }

    private fun resolveErrorType(it: Throwable) = when (it) {
        is WebException.BadVerificationCodeException -> LoadingErrorType.LOGIN_ERROR
        else -> LoadingErrorType.GENERIC_ERROR
    }
}
