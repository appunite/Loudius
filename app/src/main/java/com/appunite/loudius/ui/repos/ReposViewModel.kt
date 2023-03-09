package com.appunite.loudius.ui.repos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.BuildConfig
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    fun getAccessToken(code: String) {
        viewModelScope.launch {
            authRepository.fetchAccessToken(
                clientId = CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                code = code,
            ).onSuccess { token ->
                Log.i("access_token", token.accessToken.toString())
            }.onFailure {
                Log.i("access_token", it.message.toString())
            }
        }
    }
}
