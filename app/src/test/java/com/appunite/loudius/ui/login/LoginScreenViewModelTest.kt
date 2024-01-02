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

package com.appunite.loudius.ui.login

import com.appunite.loudius.analytics.EventTracker
import com.appunite.loudius.analytics.events.ClickLogInEvent
import com.appunite.loudius.analytics.events.LogInScreenOpenedEvent
import com.appunite.loudius.analytics.events.OpenGithubAuthEvent
import com.appunite.loudius.analytics.events.ShowXiaomiPermissionDialogEvent
import com.appunite.loudius.analytics.events.XiaomiPermissionDialogDismissedEvent
import com.appunite.loudius.analytics.events.XiaomiPermissionDialogPermissionAlreadyGrantedEvent
import com.appunite.loudius.analytics.events.XiaomiPermissionDialogPermissionGrantedEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import strikt.assertions.isTrue

class LoginScreenViewModelTest {

    private val githubHelper = mockk<GithubHelper> {
        every { shouldAskForXiaomiIntent() } returns false
    }
    private val eventTracker = mockk<EventTracker>(relaxed = true)
    private fun create() = LoginScreenViewModel(githubHelper, eventTracker)

    @Test
    fun `WHEN log-in click THEN open github authorization`() {
        val viewModel = create()

        viewModel.onAction(LoginAction.ClickLogIn)

        expectThat(viewModel.state) {
            get(LoginState::navigateTo).isEqualTo(LoginNavigateTo.OpenGithubAuth)
            get(LoginState::showXiaomiPermissionDialog).isFalse()
        }

        verifyOrder {
            eventTracker.trackEvent(LogInScreenOpenedEvent)
            eventTracker.trackEvent(ClickLogInEvent)
            eventTracker.trackEvent(OpenGithubAuthEvent)
        }
    }

    @Test
    fun `GIVEN should ask for xiaomi intent WHEN log-in click THEN show xiaomi permission dialog`() {
        every { githubHelper.shouldAskForXiaomiIntent() } returns true
        val viewModel = create()

        viewModel.onAction(LoginAction.ClickLogIn)

        expectThat(viewModel.state) {
            get(LoginState::navigateTo).isNull()
            get(LoginState::showXiaomiPermissionDialog).isTrue()
        }

        verifyOrder {
            eventTracker.trackEvent(LogInScreenOpenedEvent)
            eventTracker.trackEvent(ClickLogInEvent)
            eventTracker.trackEvent(ShowXiaomiPermissionDialogEvent)
        }
    }

    @Test
    fun `GIVEN xiaomi permission dialog is displayed WHEN dismisses dialog THEN hide the dialog`() {
        every { githubHelper.shouldAskForXiaomiIntent() } returns true
        val viewModel = create()
        viewModel.onAction(LoginAction.ClickLogIn)
        expectThat(viewModel.state).get(LoginState::showXiaomiPermissionDialog).isTrue()

        viewModel.onAction(LoginAction.XiaomiPermissionDialogDismiss)

        expectThat(viewModel.state) {
            get(LoginState::navigateTo).isNull()
            get(LoginState::showXiaomiPermissionDialog).isFalse()
        }

        verifyOrder {
            eventTracker.trackEvent(LogInScreenOpenedEvent)
            eventTracker.trackEvent(ClickLogInEvent)
            eventTracker.trackEvent(ShowXiaomiPermissionDialogEvent)
            eventTracker.trackEvent(XiaomiPermissionDialogDismissedEvent)
        }
    }

    @Test
    fun `GIVEN xiaomi permission dialog is displayed WHEN grant permission THEN navigate to xiaomi permissions manager`() {
        every { githubHelper.shouldAskForXiaomiIntent() } returns true
        val viewModel = create()
        viewModel.onAction(LoginAction.ClickLogIn)
        expectThat(viewModel.state).get(LoginState::showXiaomiPermissionDialog).isTrue()

        viewModel.onAction(LoginAction.XiaomiPermissionDialogGrantPermission)

        expectThat(viewModel.state) {
            get(LoginState::navigateTo).isEqualTo(LoginNavigateTo.OpenXiaomiPermissionManager)
            get(LoginState::showXiaomiPermissionDialog).isTrue()
        }

        verifyOrder {
            eventTracker.trackEvent(LogInScreenOpenedEvent)
            eventTracker.trackEvent(ClickLogInEvent)
            eventTracker.trackEvent(ShowXiaomiPermissionDialogEvent)
            eventTracker.trackEvent(XiaomiPermissionDialogPermissionGrantedEvent)
        }
    }

    @Test
    fun `GIVEN xiaomi permission dialog is displayed WHEN user click already granted THEN navigate github auth`() {
        every { githubHelper.shouldAskForXiaomiIntent() } returns true
        val viewModel = create()
        viewModel.onAction(LoginAction.ClickLogIn)
        expectThat(viewModel.state).get(LoginState::showXiaomiPermissionDialog).isTrue()

        viewModel.onAction(LoginAction.XiaomiPermissionDialogAlreadyGrantedPermission)

        expectThat(viewModel.state) {
            get(LoginState::navigateTo).isEqualTo(LoginNavigateTo.OpenGithubAuth)
            get(LoginState::showXiaomiPermissionDialog).isFalse()
        }

        verifyOrder {
            eventTracker.trackEvent(LogInScreenOpenedEvent)
            eventTracker.trackEvent(ClickLogInEvent)
            eventTracker.trackEvent(ShowXiaomiPermissionDialogEvent)
            eventTracker.trackEvent(XiaomiPermissionDialogPermissionAlreadyGrantedEvent)
            eventTracker.trackEvent(OpenGithubAuthEvent)
        }
    }

    @Test
    fun `test the whole xiaomi flow`() {
        // on Xiaomin with GitHub App
        every { githubHelper.shouldAskForXiaomiIntent() } returns true
        val viewModel = create()

        // When user click log-in screen
        viewModel.onAction(LoginAction.ClickLogIn)

        // Then display xiaomi permission dialog
        expectThat(viewModel.state) {
            get(LoginState::showXiaomiPermissionDialog).isTrue()
            get(LoginState::navigateTo).isNull()
        }

        // When user clicks grant permission
        viewModel.onAction(LoginAction.XiaomiPermissionDialogGrantPermission)

        // Then show xiaomi preferences menager
        expectThat(viewModel.state) {
            get(LoginState::showXiaomiPermissionDialog).isTrue()
            get(LoginState::navigateTo).isEqualTo(LoginNavigateTo.OpenXiaomiPermissionManager)
        }

        // When user goes back to the app and click button with granted info
        viewModel.onAction(LoginAction.XiaomiPermissionDialogAlreadyGrantedPermission)

        // Then show xiaomi preferences menager
        expectThat(viewModel.state) {
            get(LoginState::showXiaomiPermissionDialog).isFalse()
            get(LoginState::navigateTo).isEqualTo(LoginNavigateTo.OpenGithubAuth)
        }

        verifyOrder {
            eventTracker.trackEvent(LogInScreenOpenedEvent)
            eventTracker.trackEvent(ClickLogInEvent)
            eventTracker.trackEvent(ShowXiaomiPermissionDialogEvent)
            eventTracker.trackEvent(XiaomiPermissionDialogPermissionGrantedEvent)
            eventTracker.trackEvent(XiaomiPermissionDialogPermissionAlreadyGrantedEvent)
            eventTracker.trackEvent(OpenGithubAuthEvent)
        }
    }
}
