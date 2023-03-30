package com.appunite.loudius.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appunite.loudius.network.utils.AuthFailureHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

data class MainState(
    val authFailureEvent: Unit? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(private val authFailureHandler: AuthFailureHandler) :
    ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    init {
        viewModelScope.launch {
            authFailureHandler.authFailureFlow.collect {
                state = MainState(Unit)
            }
        }
    }

    fun onAuthFailureHandled() {
        state = MainState(null)
    }
}
