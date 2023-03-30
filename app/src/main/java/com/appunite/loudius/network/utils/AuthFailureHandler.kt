package com.appunite.loudius.network.utils

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface AuthFailureHandler {
    suspend fun emitAuthFailure()

    val authFailureFlow: SharedFlow<Unit>
}

@Singleton
class AuthFailureHandlerImpl @Inject constructor() : AuthFailureHandler {

    private val _authFailureFlow = MutableSharedFlow<Unit>()
    override val authFailureFlow: SharedFlow<Unit> = _authFailureFlow

    override suspend fun emitAuthFailure() {
        _authFailureFlow.emit(Unit)
    }
}
