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

package com.appunite.loudius.ui.loading

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.util.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class LoadingViewModelTest {

    companion object {
    }

    private val repository: FakeAuthRepository = FakeAuthRepository()
    private val savedStateHandle = SavedStateHandle()

    private fun create() = LoadingViewModel(repository, savedStateHandle)

    @Test
    fun `GIVEN valid code WHEN setCodeAndGetAccessToken THEN set code, access token and navigate to pull requests`() {
        setupIntent()
        val viewModel = create()

        assertEquals("validCode", viewModel.state.code)
        assertEquals("validToken", viewModel.state.accessToken)
        assertEquals(LoadingScreenNavigation.NavigateToPullRequests, viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN OnTryAgain action WHEN onAction THEN set showErrorScreen and get access token`() {
        setupIntent()
        val viewModel = create()
        val action = LoadingAction.OnTryAgainClick

        viewModel.onAction(action)

        assertNull(viewModel.state.errorScreenType)
        assertEquals("validToken", viewModel.state.accessToken)
    }

    @Test
    fun `GIVEN OnNavigateToPullRequests action WHEN onAction THEN set navigateToPullRequests to null`() {
        setupIntent()
        val viewModel = create()
        val action = LoadingAction.OnNavigate

        viewModel.onAction(action)

        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN invalid code WHEN setCodeAndGetAccessToken THEN show login error screen`() {
        setupIntent(intentCode = "invalidCode")
        val viewModel = create()

        assertEquals(LoadingErrorType.LOGIN_ERROR, viewModel.state.errorScreenType)
        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN unexpected Github behavior WHEN setCodeAndGetAccessToken THEN show generic error screen`() {
        setupIntent(intentCode = "codeLeadingToUnexpectedError")
        val viewModel = create()

        assertEquals(LoadingErrorType.GENERIC_ERROR, viewModel.state.errorScreenType)
        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN retry click WHEN logging in error THEN redirect to login screen`() {
        setupIntent(intentCode = "invalidCode")
        val viewModel = create()

        viewModel.onAction(LoadingAction.OnTryAgainClick)

        assertEquals(LoadingScreenNavigation.NavigateToLogin, viewModel.state.navigateTo)
    }

    private fun setupIntent(intentCode: String = "validCode") {
        val uri = mockk<Uri> {
            every { getQueryParameter("code") } returns intentCode
        }
        val intent = mockk<Intent> {
            every { data } returns uri
        }
        savedStateHandle[NavController.KEY_DEEP_LINK_INTENT] = intent
    }
}
