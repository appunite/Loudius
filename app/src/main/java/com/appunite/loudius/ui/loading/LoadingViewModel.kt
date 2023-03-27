package com.appunite.loudius.ui.loading

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.BuildConfig
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.domain.repository.AuthRepository
import com.appunite.loudius.network.datasource.BadVerificationCodeException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

sealed class LoadingAction {

    object OnNavigate : LoadingAction()

    object OnTryAgainClick : LoadingAction()
}

enum class LoadingErrorType {
    LOGIN_ERROR,
    GENERIC_ERROR,
}

data class LoadingState(
    val accessToken: String? = null,
    val code: String? = null,
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
) : ViewModel() {

    var state by mutableStateOf(LoadingState())
        private set

    fun setCodeAndGetAccessToken(code: String) {
        state = state.copy(code = code)
        getAccessToken(code)
    }

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
        is BadVerificationCodeException -> LoadingErrorType.LOGIN_ERROR
        else -> LoadingErrorType.GENERIC_ERROR
    }
}
