package com.appunite.loudius.ui.repos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.BuildConfig
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    fun getAccessToken(code: String) {
        viewModelScope.launch {
            userRepository.fetchAccessToken(
                clientId = CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                code = code,
            ).onSuccess { token ->
                Log.i("access_token", token)
            }.onFailure {
                Log.i("access_token", it.message.toString())
            }
        }
    }
}
