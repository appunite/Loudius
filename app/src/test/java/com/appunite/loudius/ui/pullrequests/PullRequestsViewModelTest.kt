@file:OptIn(ExperimentalCoroutinesApi::class)

package com.appunite.loudius.ui.pullrequests

import com.appunite.loudius.fakes.FakePullRequestRepository
import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.Defaults
import com.appunite.loudius.util.MainDispatcherExtension
import com.appunite.loudius.utils.neverCompletingSuspension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class PullRequestsViewModelTest {
    private val pullRequestRepository = FakePullRequestRepository()
    private fun createViewModel() = PullRequestsViewModel(pullRequestRepository)

    @Test
    fun `WHEN init THEN display loading`() = runTest {
        pullRequestRepository.setCurrentUserPullRequests { neverCompletingSuspension() }
        val viewModel = createViewModel()

        assertTrue(viewModel.state.isLoading)
    }

    @Test
    fun `WHEN init THEN display pull requests list`() = runTest {
        val viewModel = createViewModel()

        assertEquals(listOf(Defaults.pullRequest()), viewModel.state.pullRequests)
        assertFalse(viewModel.state.isLoading)
    }

    @Test
    fun `WHEN fetching data failed on init THEN display error`() = runTest {
        pullRequestRepository.setCurrentUserPullRequests { Result.failure(WebException.NetworkError()) }
        val viewModel = createViewModel()

        assertEquals(emptyList<PullRequest>(), viewModel.state.pullRequests)
        assertTrue(viewModel.state.isError)
    }

    @Test
    fun `GIVEN error state WHEN retry THEN fetch pull requests list again`() = runTest {
        pullRequestRepository.setCurrentUserPullRequests { Result.failure(WebException.NetworkError()) }
        val viewModel = createViewModel()

        pullRequestRepository.resetCurrentUserPullRequestAnswer()
        viewModel.onAction(PulLRequestsAction.RetryClick)

        assertEquals(listOf(Defaults.pullRequest()), viewModel.state.pullRequests)
        assertFalse(viewModel.state.isLoading)
    }

    @Test
    fun `GIVEN item id WHEN item click THEN navigate the user to reviewers`() = runTest {
        val viewModel = createViewModel()
        assertNull(viewModel.state.navigateToReviewers)
        val pullRequest = Defaults.pullRequest()

        viewModel.onAction(PulLRequestsAction.ItemClick(pullRequest.id))

        val expected = NavigationPayload(
            pullRequest.owner,
            pullRequest.shortRepositoryName,
            pullRequest.number.toString(),
            pullRequest.createdAt.toString(),
        )
        assertEquals(expected, viewModel.state.navigateToReviewers)
    }

    @Test
    fun `GIVEN navigation payload WHEN navigating to reviewers THEN reset payload`() = runTest {
        val viewModel = createViewModel()
        val pullRequest = Defaults.pullRequest()
        viewModel.onAction(PulLRequestsAction.ItemClick(pullRequest.id))

        viewModel.onAction(PulLRequestsAction.OnNavigateToReviewers)

        assertNull(viewModel.state.navigateToReviewers)
    }
}