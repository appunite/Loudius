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

package com.appunite.loudius.ui.authenticating

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.MainDispatcherExtension
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class AuthenticatingViewModelTest {

    private val repository: FakeAuthRepository = spyk(FakeAuthRepository())
    private val savedStateHandle = SavedStateHandle()

    private fun create() = AuthenticatingViewModel(repository, savedStateHandle)

    @Test
    fun `GIVEN valid code WHEN authenticated THEN navigate to pull requests screen`() {
        setupIntent("validCode")
        val viewModel = create()

        assertNull(viewModel.state.errorScreenType)
        assertEquals(AuthenticatingScreenNavigation.NavigateToPullRequests, viewModel.state.navigateTo)

        viewModel.onAction(AuthenticatingAction.OnNavigate)
        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN invalid code WHEN authenticating screen is opened THEN show login error screen`() {
        setupIntent("invalidCode")
        val viewModel = create()

        assertEquals(LoadingErrorType.LOGIN_ERROR, viewModel.state.errorScreenType)
        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN unexpected Github behavior WHEN authenticating screen is opened THEN show generic error screen`() {
        coEvery { repository.fetchAccessToken(any(), any(), any())} returns  Result.failure(
            WebException.UnknownError(null, null))
        setupIntent("validCode")
        val viewModel = create()

        assertEquals(LoadingErrorType.GENERIC_ERROR, viewModel.state.errorScreenType)
        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN unexpected error is presented WHEN try again success THEN navigate to pull requests`() {
        // simulate unknown error response
        coEvery { repository.fetchAccessToken(any(), any(), any())} returns  Result.failure(
            WebException.UnknownError(null, null))
        setupIntent("validCode")
        val viewModel = create()

        // ensure error screen is displayed
        assertEquals(LoadingErrorType.GENERIC_ERROR, viewModel.state.errorScreenType)
        assertNull(viewModel.state.navigateTo)

        // simulate success response and click try again
        clearMocks(repository)
        viewModel.onAction(AuthenticatingAction.OnTryAgainClick)

        // ensure user is navigated to the pull requests screen
        assertEquals(AuthenticatingScreenNavigation.NavigateToPullRequests, viewModel.state.navigateTo)
        assertNull(viewModel.state.errorScreenType)

        // clear the navigation state
        viewModel.onAction(AuthenticatingAction.OnNavigate)
        assertNull(viewModel.state.navigateTo)
        assertNull(viewModel.state.errorScreenType)
    }

    @Test
    fun `GIVEN invalid screen and error is presented WHEN retry click THEN navigate to the login screen`() {
        setupIntent("invalidCode")
        val viewModel = create()
        assertEquals(LoadingErrorType.LOGIN_ERROR, viewModel.state.errorScreenType)

        viewModel.onAction(AuthenticatingAction.OnTryAgainClick)

        assertEquals(AuthenticatingScreenNavigation.NavigateToLogin, viewModel.state.navigateTo)
        viewModel.onAction(AuthenticatingAction.OnNavigate)
        assertNull(viewModel.state.navigateTo)
        assertEquals(LoadingErrorType.LOGIN_ERROR, viewModel.state.errorScreenType)
    }

    @Test
    fun `GIVEN no intent is provided WHEN screen is presented THEN show login error`() {
        savedStateHandle[NavController.KEY_DEEP_LINK_INTENT] = null
        val viewModel = create()

        assertEquals(LoadingErrorType.LOGIN_ERROR, viewModel.state.errorScreenType)
        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN no intent is provided and login error is presented WHEN retry is clicked THEN navigate to the login screen`() {
        savedStateHandle[NavController.KEY_DEEP_LINK_INTENT] = null
        val viewModel = create()
        assertNull(viewModel.state.navigateTo)
        assertEquals(LoadingErrorType.LOGIN_ERROR, viewModel.state.errorScreenType)

        viewModel.onAction(AuthenticatingAction.OnTryAgainClick)

        assertEquals(AuthenticatingScreenNavigation.NavigateToLogin, viewModel.state.navigateTo)
        assertEquals(LoadingErrorType.LOGIN_ERROR, viewModel.state.errorScreenType)

        viewModel.onAction(AuthenticatingAction.OnNavigate)

        assertNull(viewModel.state.navigateTo)
        assertEquals(LoadingErrorType.LOGIN_ERROR, viewModel.state.errorScreenType)
    }

    private fun setupIntent(intentCode: String) {
        val uri = mockk<Uri> {
            every { getQueryParameter("code") } returns intentCode
        }
        val intent = mockk<Intent> {
            every { data } returns uri
        }
        savedStateHandle[NavController.KEY_DEEP_LINK_INTENT] = intent
    }
}
