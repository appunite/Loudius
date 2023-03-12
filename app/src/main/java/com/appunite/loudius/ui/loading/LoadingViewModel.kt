package com.appunite.loudius.ui.loading

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.BuildConfig
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoadingState(
    val accessToken: String? = null,
    val showErrorScreen: Boolean = false,
)

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var state by mutableStateOf(LoadingState())
        private set

    fun getAccessToken(code: String) {
        viewModelScope.launch {
            authRepository.fetchAccessToken(
                clientId = CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                code = code,
            ).onSuccess { token ->
                state = if (token.accessToken != null) {
                    state.copy(accessToken = token.accessToken)
                } else {
                    state.copy(showErrorScreen = true)
                }
            }.onFailure {
                state = state.copy(showErrorScreen = true)
            }
        }
    }
}
