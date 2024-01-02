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

package com.appunite.loudius.ui.pullrequests

import com.appunite.loudius.analytics.EventTracker
import com.appunite.loudius.analytics.events.FetchPullRequestsEvent
import com.appunite.loudius.analytics.events.FetchPullRequestsFailureEvent
import com.appunite.loudius.analytics.events.FetchPullRequestsSuccessEvent
import com.appunite.loudius.analytics.events.NavigateToReviewersEvent
import com.appunite.loudius.analytics.events.RefreshPullRequestsEvent
import com.appunite.loudius.analytics.events.RefreshPullRequestsSuccessEvent
import com.appunite.loudius.fakes.FakePullRequestRepository
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.Defaults
import com.appunite.loudius.util.MainDispatcherExtension
import com.appunite.loudius.util.neverCompletingSuspension
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyOrder
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import strikt.assertions.isTrue
import strikt.assertions.single

@ExtendWith(MainDispatcherExtension::class)
class PullRequestsViewModelTest {
    private val pullRequestRepository = spyk(FakePullRequestRepository())
    private val eventTracker = mockk<EventTracker>(relaxed = true)
    private fun createViewModel() = PullRequestsViewModel(pullRequestRepository, eventTracker)

    @Test
    fun `WHEN refresh data THEN start refreshing data and set isRefreshing to true`() = runTest {
        val viewModel = createViewModel()

        coEvery {
            pullRequestRepository.getCurrentUserPullRequests()
        } coAnswers { neverCompletingSuspension() }

        viewModel.refreshData()

        expectThat(viewModel.isRefreshing).isTrue()

        verifyOrder {
            eventTracker.trackEvent(FetchPullRequestsEvent)
            eventTracker.trackEvent(FetchPullRequestsSuccessEvent)
            eventTracker.trackEvent(RefreshPullRequestsEvent)
        }
    }

    @Test
    fun `WHEN refresh data THEN refresh data and set isRefreshing to false`() = runTest {
        val viewModel = createViewModel()

        viewModel.refreshData()

        expectThat(viewModel.isRefreshing).isFalse()

        verifyOrder {
            eventTracker.trackEvent(FetchPullRequestsEvent)
            eventTracker.trackEvent(FetchPullRequestsSuccessEvent)
            eventTracker.trackEvent(RefreshPullRequestsEvent)
            eventTracker.trackEvent(RefreshPullRequestsSuccessEvent)
        }
    }

    @Test
    fun `WHEN refresh data THEN refresh data and display pull requests`() = runTest {
        val viewModel = createViewModel()

        viewModel.refreshData()

        expectThat(viewModel.state.data).isA<Data.Success>().and {
            get(Data.Success::pullRequests).single().isEqualTo(Defaults.pullRequest())
        }

        verifyOrder {
            eventTracker.trackEvent(FetchPullRequestsEvent)
            eventTracker.trackEvent(FetchPullRequestsSuccessEvent)
            eventTracker.trackEvent(RefreshPullRequestsEvent)
            eventTracker.trackEvent(RefreshPullRequestsSuccessEvent)
        }
    }

    @Test
    fun `WHEN init THEN display loading`() = runTest {
        coEvery { pullRequestRepository.getCurrentUserPullRequests() } coAnswers { neverCompletingSuspension() }

        val viewModel = createViewModel()

        expectThat(viewModel.state.data).isA<Data.Loading>()

        verifyOrder {
            eventTracker.trackEvent(FetchPullRequestsEvent)
        }
    }

    @Test
    fun `WHEN init THEN display pull requests list`() = runTest {
        val viewModel = createViewModel()

        expectThat(viewModel.state.data).isA<Data.Success>().and {
            get(Data.Success::pullRequests).single().isEqualTo(Defaults.pullRequest())
        }

        verifyOrder {
            eventTracker.trackEvent(FetchPullRequestsEvent)
            eventTracker.trackEvent(FetchPullRequestsSuccessEvent)
        }
    }

    @Test
    fun `WHEN fetching data failed on init THEN display error`() = runTest {
        coEvery { pullRequestRepository.getCurrentUserPullRequests() } coAnswers {
            Result.failure(
                WebException.NetworkError()
            )
        }
        val viewModel = createViewModel()

        expectThat(viewModel.state.data).isA<Data.Error>()

        verifyOrder {
            eventTracker.trackEvent(FetchPullRequestsEvent)
            eventTracker.trackEvent(FetchPullRequestsFailureEvent)
        }
    }

    @Test
    fun `GIVEN error state WHEN retry THEN fetch pull requests list again`() = runTest {
        coEvery { pullRequestRepository.getCurrentUserPullRequests() } coAnswers {
            Result.failure(
                WebException.NetworkError()
            )
        }
        val viewModel = createViewModel()

        clearMocks(pullRequestRepository)
        viewModel.onAction(PulLRequestsAction.RetryClick)

        expectThat(viewModel.state.data).isA<Data.Success>().and {
            get(Data.Success::pullRequests).single().isEqualTo(Defaults.pullRequest())
        }

        verifyOrder {
            eventTracker.trackEvent(FetchPullRequestsEvent)
            eventTracker.trackEvent(FetchPullRequestsFailureEvent)
            eventTracker.trackEvent(FetchPullRequestsEvent)
            eventTracker.trackEvent(FetchPullRequestsSuccessEvent)
        }
    }

    @Test
    fun `GIVEN item id WHEN item click THEN navigate the user to reviewers`() = runTest {
        val viewModel = createViewModel()
        expectThat(viewModel.state)
            .get(PullRequestState::navigateToReviewers)
            .isNull()
        val pullRequest = Defaults.pullRequest()

        viewModel.onAction(PulLRequestsAction.ItemClick(pullRequest.id))

        expectThat(viewModel.state)
            .get(PullRequestState::navigateToReviewers)
            .isEqualTo(
                NavigationPayload(
                    pullRequest.owner,
                    pullRequest.shortRepositoryName,
                    pullRequest.number.toString(),
                    pullRequest.createdAt.toString()
                )
            )

        verifyOrder {
            eventTracker.trackEvent(FetchPullRequestsEvent)
            eventTracker.trackEvent(FetchPullRequestsSuccessEvent)
            eventTracker.trackEvent(NavigateToReviewersEvent)
        }
    }

    @Test
    fun `GIVEN navigation payload WHEN navigating to reviewers THEN reset payload`() = runTest {
        val viewModel = createViewModel()
        val pullRequest = Defaults.pullRequest()
        viewModel.onAction(PulLRequestsAction.ItemClick(pullRequest.id))

        viewModel.onAction(PulLRequestsAction.OnNavigateToReviewers)

        expectThat(viewModel.state)
            .get(PullRequestState::navigateToReviewers)
            .isNull()

        verifyOrder {
            eventTracker.trackEvent(FetchPullRequestsEvent)
            eventTracker.trackEvent(FetchPullRequestsSuccessEvent)
            eventTracker.trackEvent(NavigateToReviewersEvent)
        }
    }
}
