package com.appunite.loudius.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.domain.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val githubRepository: GithubRepository
): ViewModel() {

    fun onLoginClick() {
        viewModelScope.launch {
            githubRepository.authorize().onSuccess { code ->
                githubRepository.getAccessToken(CLIENT_ID,"", code).onSuccess { token -> //TODO add client secret
                    Log.i("access_token", token.accessToken)
                }
            }
        }
    }
}
