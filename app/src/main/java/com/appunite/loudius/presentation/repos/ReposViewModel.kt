package com.appunite.loudius.presentation.repos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.domain.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val githubRepository: GithubRepository
) : ViewModel() {

    fun getAccessToken(code: String) {
        viewModelScope.launch {
            // TODO add client secret [SIL-66]
            githubRepository.getAccessToken(
                clientId = CLIENT_ID,
                clientSecret = "",
                code = code
            ).onSuccess { token ->
                Log.i("access_token", token.accessToken)
            }.onFailure {
                Log.i("access_token", it.message.toString())
            }
        }
    }
}
