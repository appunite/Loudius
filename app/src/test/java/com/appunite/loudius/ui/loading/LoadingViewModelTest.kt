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

import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.util.MainDispatcherExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class LoadingViewModelTest {

    companion object {
        private const val EXAMPLE_CODE = "validCode"
        private const val EXAMPLE_INVALID_CODE = "invalidCode"
        private const val EXAMPLE_CODE_LEADING_TO_UNEXPECTED_ERROR = "codeLeadingToUnexpectedError"
        private const val EXAMPLE_ACCESS_TOKEN = "validToken"
    }

    private val repository: FakeAuthRepository = FakeAuthRepository()
    private val viewModel = LoadingViewModel(repository)

    @Test
    fun `GIVEN valid code WHEN setCodeAndGetAccessToken THEN set code, access token and navigate to pull requests`() {
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE)

        assertEquals(EXAMPLE_CODE, viewModel.state.code)
        assertEquals(EXAMPLE_ACCESS_TOKEN, viewModel.state.accessToken)
        assertEquals(LoadingScreenNavigation.NavigateToPullRequests, viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN OnTryAgain action WHEN onAction THEN set showErrorScreen and get access token`() {
        val action = LoadingAction.OnTryAgainClick
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE)

        viewModel.onAction(action)

        assertNull(viewModel.state.errorScreenType)
        assertEquals(EXAMPLE_ACCESS_TOKEN, viewModel.state.accessToken)
    }

    @Test
    fun `GIVEN OnNavigateToPullRequests action WHEN onAction THEN set navigateToPullRequests to null`() {
        val action = LoadingAction.OnNavigate
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE)

        viewModel.onAction(action)

        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN invalid code WHEN setCodeAndGetAccessToken THEN show login error screen`() {
        viewModel.setCodeAndGetAccessToken(EXAMPLE_INVALID_CODE)

        assertEquals(LoadingErrorType.LOGIN_ERROR, viewModel.state.errorScreenType)
        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN unexpected Github behavior WHEN setCodeAndGetAccessToken THEN show generic error screen`() {
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE_LEADING_TO_UNEXPECTED_ERROR)

        assertEquals(LoadingErrorType.GENERIC_ERROR, viewModel.state.errorScreenType)
        assertNull(viewModel.state.navigateTo)
    }

    @Test
    fun `GIVEN retry click WHEN logging in error THEN redirect to login screen`() {
        viewModel.setCodeAndGetAccessToken(EXAMPLE_INVALID_CODE)

        viewModel.onAction(LoadingAction.OnTryAgainClick)

        assertEquals(LoadingScreenNavigation.NavigateToLogin, viewModel.state.navigateTo)
    }
}
