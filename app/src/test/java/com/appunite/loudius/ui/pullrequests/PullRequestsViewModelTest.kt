@file:OptIn(ExperimentalCoroutinesApi::class)

package com.appunite.loudius.ui.pullrequests

import com.appunite.loudius.fakes.FakePullRequestRepository
import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.MainDispatcherExtension
import com.appunite.loudius.utils.neverCompletingSuspension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MainDispatcherExtension::class)
class PullRequestsViewModelTest {
    private val pullRequestRepository = FakePullRequestRepository()
    private fun getViewModel() = PullRequestsViewModel(pullRequestRepository)

    @Test
    fun `GIVEN logged in user WHEN init THEN display loading`() = runTest {
        pullRequestRepository.setCurrentUserPullRequests { neverCompletingSuspension() }
        val viewModel = getViewModel()

        assertTrue(viewModel.state.isLoading)
    }

    @Test
    fun `GIVEN logged in user WHEN init THEN display pull requests list`() {
        val viewModel = getViewModel()

        assertEquals(
            listOf(
                PullRequest(
                    id = 1,
                    draft = false,
                    number = 1,
                    repositoryUrl = "https://api.github.com/repos/exampleOwner/exampleRepo",
                    title = "example title",
                    LocalDateTime.parse("2023-03-07T09:21:45"),
                ),
            ),
            viewModel.state.pullRequests,
        )
        assertFalse(viewModel.state.isLoading)
    }

    @Test
    fun `GIVEN logged in user WHEN fetching failed THEN display error`() = runTest {
        pullRequestRepository.setCurrentUserPullRequests { Result.failure(WebException.NetworkError()) }
        val viewModel = getViewModel()

        assertTrue(viewModel.state.isError)
    }

    @Test
    fun `GIVEN logged in user WHEN retry THEN fetch pull requests list again`() {
        pullRequestRepository.setCurrentUserPullRequests { Result.failure(WebException.NetworkError()) }
        val viewModel = getViewModel()
        assertTrue(viewModel.state.isError)
        assertEquals(emptyList<PullRequest>(), viewModel.state.pullRequests)

        pullRequestRepository.resetCurrentUserPullRequestAnswer()
        viewModel.onAction(PulLRequestsAction.RetryClick)

        assertEquals(
            listOf(
                PullRequest(
                    id = 1,
                    draft = false,
                    number = 1,
                    repositoryUrl = "https://api.github.com/repos/exampleOwner/exampleRepo",
                    title = "example title",
                    LocalDateTime.parse("2023-03-07T09:21:45"),
                ),
            ),
            viewModel.state.pullRequests,
        )
        assertFalse(viewModel.state.isLoading)
    }
}
