package com.appunite.loudius.ui.loading

import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.util.MainDispatcherExtension
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock

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
        mockkStatic(Clock::class)
        viewModel = LoadingViewModel(repository)
    }

    @Test
    fun `GIVEN example valid code WHEN setCodeAndGetAccessToken THEN set code, access token and navigateToPullRequests`() {
        //when
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE)

        //then
        assert(viewModel.state.code == EXAMPLE_CODE)
        assert(viewModel.state.accessToken == EXAMPLE_ACCESS_TOKEN)
        assert(viewModel.state.navigateToPullRequests == NavigateToPullRequests)
    }

    @Test
    fun `GIVEN null as code WHEN setCodeAndGetAccessToken THEN set null as code`() {
        //when
        viewModel.setCodeAndGetAccessToken(null)

        //then
        assert(viewModel.state.code == null)
    }

    @Test
    fun `GIVEN OnTryAgain action WHEN onAction THEN set showErrorScreen and get access token`() {
        //given
        val action = LoadingAction.OnTryAgainClick
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE)

        //when
        viewModel.onAction(action)

        //then
        assert(!viewModel.state.showErrorScreen)
        assert(viewModel.state.accessToken == EXAMPLE_ACCESS_TOKEN)
    }

    @Test
    fun `GIVEN OnNavigateToPullRequests action WHEN onAction THEN set navigateToPullRequests as null`() {
        //given
        val action = LoadingAction.OnNavigateToPullRequests
        viewModel.setCodeAndGetAccessToken(EXAMPLE_CODE)

        //when
        viewModel.onAction(action)

        //then
        assert(viewModel.state.navigateToPullRequests == null)
    }
}
