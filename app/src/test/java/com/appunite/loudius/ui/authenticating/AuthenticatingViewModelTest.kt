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
import com.appunite.loudius.analytics.EventTracker
import com.appunite.loudius.analytics.events.AuthenticatingEvents
import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.MainDispatcherExtension
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

@ExtendWith(MainDispatcherExtension::class)
class AuthenticatingViewModelTest {

    private val repository: FakeAuthRepository = spyk(FakeAuthRepository())
    private val savedStateHandle = SavedStateHandle()
    private val eventTracker = mockk<EventTracker>(relaxed = true)

    private fun create() = AuthenticatingViewModel(repository, savedStateHandle, eventTracker)

    @Test
    fun `GIVEN valid code WHEN authenticated THEN navigate to pull requests screen`() {
        setupIntent("validCode")
        val viewModel = create()

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isNull()
            get(AuthenticatingState::navigateTo).isEqualTo(AuthenticatingScreenNavigation.NavigateToPullRequests)
        }

        viewModel.onAction(AuthenticatingAction.OnNavigate)

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isNull()
            get(AuthenticatingState::navigateTo).isNull()
        }

        verifyOrder {
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationStarted)
            eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenFinishedSuccess)
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedSuccess)
        }
    }

    @Test
    fun `GIVEN invalid code WHEN authenticating screen is opened THEN show login error screen`() {
        setupIntent("invalidCode")
        val viewModel = create()

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.LOGIN_ERROR)
            get(AuthenticatingState::navigateTo).isNull()
        }

        verifyOrder {
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationStarted)
            eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenFinishedFailure("Unrecognised error."))
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedFailure("Unrecognised error."))
        }
    }

    @Test
    fun `GIVEN unexpected Github behavior WHEN authenticating screen is opened THEN show generic error screen`() {
        coEvery { repository.fetchAccessToken(any(), any(), any()) } returns Result.failure(
            WebException.UnknownError(null, null)
        )
        setupIntent("validCode")
        val viewModel = create()

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.GENERIC_ERROR)
            get(AuthenticatingState::navigateTo).isNull()
        }

        verifyOrder {
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationStarted)
            eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenFinishedFailure("Unrecognised error."))
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedFailure("Unrecognised error."))
        }
    }

    @Test
    fun `GIVEN unexpected error is presented WHEN try again success THEN navigate to pull requests`() {
        // simulate unknown error response
        coEvery { repository.fetchAccessToken(any(), any(), any()) } returns Result.failure(
            WebException.UnknownError(null, null)
        )
        setupIntent("validCode")
        val viewModel = create()

        // ensure error screen is displayed
        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.GENERIC_ERROR)
            get(AuthenticatingState::navigateTo).isNull()
        }

        // simulate success response and click try again
        clearMocks(repository)
        viewModel.onAction(AuthenticatingAction.OnTryAgainClick)

        // ensure user is navigated to the pull requests screen
        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isNull()
            get(AuthenticatingState::navigateTo).isEqualTo(AuthenticatingScreenNavigation.NavigateToPullRequests)
        }

        // clear the navigation state
        viewModel.onAction(AuthenticatingAction.OnNavigate)
        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isNull()
            get(AuthenticatingState::navigateTo).isNull()
        }

        verifyOrder {
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationStarted)
            eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenFinishedFailure("Unrecognised error."))
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedFailure("Unrecognised error."))
            eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenStarted)
            eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenFinishedSuccess)
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedSuccess)
        }
    }

    @Test
    fun `GIVEN invalid screen and error is presented WHEN retry click THEN navigate to the login screen`() {
        setupIntent("invalidCode")
        val viewModel = create()
        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.LOGIN_ERROR)
            get(AuthenticatingState::navigateTo).isNull()
        }

        viewModel.onAction(AuthenticatingAction.OnTryAgainClick)

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.LOGIN_ERROR)
            get(AuthenticatingState::navigateTo).isEqualTo(AuthenticatingScreenNavigation.NavigateToLogin)
        }

        viewModel.onAction(AuthenticatingAction.OnNavigate)

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.LOGIN_ERROR)
            get(AuthenticatingState::navigateTo).isNull()
        }

        verifyOrder {
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationStarted)
            eventTracker.trackEvent(AuthenticatingEvents.GetAccessTokenFinishedFailure("Unrecognised error."))
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedFailure("Unrecognised error."))
        }
    }

    @Test
    fun `GIVEN no intent is provided WHEN screen is presented THEN show login error`() {
        savedStateHandle[NavController.KEY_DEEP_LINK_INTENT] = null
        val viewModel = create()

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.LOGIN_ERROR)
            get(AuthenticatingState::navigateTo).isNull()
        }

        verifyOrder {
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationStarted)
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedFailure("No error code"))
        }
    }

    @Test
    fun `GIVEN no intent is provided and login error is presented WHEN retry is clicked THEN navigate to the login screen`() {
        savedStateHandle[NavController.KEY_DEEP_LINK_INTENT] = null
        val viewModel = create()

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.LOGIN_ERROR)
            get(AuthenticatingState::navigateTo).isNull()
        }

        viewModel.onAction(AuthenticatingAction.OnTryAgainClick)

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.LOGIN_ERROR)
            get(AuthenticatingState::navigateTo).isEqualTo(AuthenticatingScreenNavigation.NavigateToLogin)
        }

        viewModel.onAction(AuthenticatingAction.OnNavigate)

        expectThat(viewModel.state) {
            get(AuthenticatingState::errorScreenType).isEqualTo(LoadingErrorType.LOGIN_ERROR)
            get(AuthenticatingState::navigateTo).isNull()
        }

        verifyOrder {
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationStarted)
            eventTracker.trackEvent(AuthenticatingEvents.AuthenticationFinishedFailure("No error code"))
        }
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
