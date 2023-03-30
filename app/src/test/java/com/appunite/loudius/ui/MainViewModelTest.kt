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

package com.appunite.loudius.ui

import com.appunite.loudius.network.utils.AuthFailureHandler
import com.appunite.loudius.network.utils.AuthFailureHandlerImpl
import com.appunite.loudius.util.MainDispatcherExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class MainViewModelTest {
    private val authFailureHandler: AuthFailureHandler = AuthFailureHandlerImpl()
    private lateinit var viewModel: MainViewModel

    @Test
    fun `WHEN init THEN auth failure event is null`() = runTest {
        viewModel = MainViewModel(authFailureHandler)
        assertEquals(null, viewModel.state.authFailureEvent)
    }

    @Test
    fun `WHEN auth failure emitted THEN set state with auth failure event`() = runTest {
        viewModel = MainViewModel(authFailureHandler)
        authFailureHandler.emitAuthFailure()

        assertEquals(Unit, viewModel.state.authFailureEvent)
    }

    @Test
    fun `WHEN auth failure handled THEN set auth failure event is null`() = runTest {
        viewModel = MainViewModel(authFailureHandler)
        authFailureHandler.emitAuthFailure()

        viewModel.onAuthFailureHandled()

        assertEquals(null, viewModel.state.authFailureEvent)
    }
}
