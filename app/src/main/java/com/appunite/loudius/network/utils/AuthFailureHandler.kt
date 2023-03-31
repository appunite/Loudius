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

package com.appunite.loudius.network.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Singleton

interface AuthFailureHandler {
    suspend fun emitAuthFailure()

    val authFailureFlow: SharedFlow<Unit>
}

@Singleton
class AuthFailureHandlerImpl : AuthFailureHandler {

    private val _authFailureFlow = MutableSharedFlow<Unit>()
    override val authFailureFlow: SharedFlow<Unit> = _authFailureFlow

    override suspend fun emitAuthFailure() {
        _authFailureFlow.emit(Unit)
    }
}