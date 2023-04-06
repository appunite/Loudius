package com.appunite.loudius.ui.login

import io.mockk.every
import io.mockk.mockk
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
    private fun create() = LoginScreenViewModel(githubHelper)

    @Test
    fun `WHEN log-in click THEN open github authorization`() {
        val viewModel = create()

        viewModel.onAction(LoginAction.ClickLogIn)

        expectThat(viewModel.state) {
            get(LoginState::navigateTo).isEqualTo(LoginNavigateTo.OpenGithubAuth)
            get(LoginState::showXiaomiPermissionDialog).isFalse()
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
            get(LoginState::showXiaomiPermissionDialog).isFalse()
        }
    }

}