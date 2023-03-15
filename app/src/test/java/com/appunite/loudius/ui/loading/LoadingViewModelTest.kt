package com.appunite.loudius.ui.loading

import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.util.MainDispatcherExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class LoadingViewModelTest {

    companion object {
        private const val EXAMPLE_CODE = "code"
        private const val EXAMPLE_ACCESS_TOKEN = "validToken"
    }

    private val repository: FakeAuthRepository = FakeAuthRepository()
    private lateinit var viewModel: LoadingViewModel

    @BeforeEach
    fun setup() {
        viewModel = LoadingViewModel(repository)
    }

    @Test
    fun `GIVEN valid code WHEN setCodeAndGetAccessToken THEN set code, access token and navigateToPullRequests`() {
        // when
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE)

        // then
        assertEquals(viewModel.state.code, EXAMPLE_CODE)
        assertEquals(viewModel.state.accessToken, EXAMPLE_ACCESS_TOKEN)
        assertEquals(viewModel.state.navigateToPullRequests, NavigateToPullRequests)
    }

    @Test
    fun `GIVEN OnTryAgain action WHEN onAction THEN set showErrorScreen and get access token`() {
        // given
        val action = LoadingAction.OnTryAgainClick
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE)

        // when
        viewModel.onAction(action)

        // then
        assertEquals(viewModel.state.showErrorScreen, false)
        assertEquals(viewModel.state.accessToken, EXAMPLE_ACCESS_TOKEN)
    }

    @Test
    fun `GIVEN OnNavigateToPullRequests action WHEN onAction THEN set navigateToPullRequests as null`() {
        // given
        val action = LoadingAction.OnNavigateToPullRequests
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE)

        // when
        viewModel.onAction(action)

        // then
        assertEquals(viewModel.state.navigateToPullRequests, null)
    }
}
