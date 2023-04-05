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

package com.appunite.loudius.ui.reviewers

import androidx.lifecycle.SavedStateHandle
import com.appunite.loudius.fakes.FakePullRequestRepository
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.MainDispatcherExtension
import com.appunite.loudius.utils.neverCompletingSuspension
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class ReviewersViewModelTest {

    private val systemNow = LocalDateTime.parse("2022-01-29T15:00:00")
    private val systemClockFixed =
        Clock.fixed(systemNow.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"))

    private val repository = spyk(FakePullRequestRepository())
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true) {
        every { get<String>(any()) } returns "example"
        every { get<String>("submission_date") } returns "2022-01-29T08:00:00"
        every { get<String>("pull_request_number") } returns "correctPullRequestNumber"
    }
    private lateinit var viewModel: ReviewersViewModel

    private fun createViewModel() = ReviewersViewModel(repository, savedStateHandle)

    @BeforeEach
    fun setup() {
        mockkStatic(Clock::class)
        every { Clock.systemDefaultZone() } returns systemClockFixed
    }

    @Nested
    inner class InitTest {
        @Test
        fun `GIVEN no values in saved state WHEN init THEN throw IllegalStateException`() {
            every { savedStateHandle.get<String>(any()) } returns null

            assertThrows<IllegalStateException> { createViewModel() }
        }

        @Test
        fun `GIVEN correct initial values WHEN init THEN all initial values are read once`() {
            viewModel = createViewModel()

            verify(exactly = 1) { savedStateHandle.get<String>("owner") }
            verify(exactly = 1) { savedStateHandle.get<String>("repo") }
            verify(exactly = 1) { savedStateHandle.get<String>("pull_request_number") }
            verify(exactly = 1) { savedStateHandle.get<String>("submission_date") }
        }

        @Test
        fun `GIVEN correct initial values WHEN init THEN pull request number is correct`() {
            viewModel = createViewModel()

            assertEquals("correctPullRequestNumber", viewModel.state.pullRequestNumber)
        }

        @Test
        fun `GIVEN correct initial values WHEN init starts THEN state is loading`() = runTest {
            coEvery { repository.getReviews(any(), any(), any()) } coAnswers { neverCompletingSuspension() }
            viewModel = createViewModel()

            assertEquals(true, viewModel.state.isLoading)
            assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
        }

        @Test
        fun `GIVEN no reviewers WHEN init THEN state is correct with no reviewers`() {
            coEvery { repository.getReviews(any(), any(), any()) } returns Result.success(emptyList())
            coEvery { repository.getRequestedReviewers(any(), any(), any()) } returns Result.success(RequestedReviewersResponse(emptyList()))

            viewModel = createViewModel()

            assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
            assertEquals(false, viewModel.state.isLoading)
        }

        @Test
        fun `GIVEN mixed reviewers WHEN init THEN list of reviewers is fetched`() =
            runTest {
                viewModel = createViewModel()

                val expected = listOf(
                    Reviewer(1, "user1", true, 7, 5),
                    Reviewer(2, "user2", false, 7, null),
                    Reviewer(3, "user3", false, 7, null),
                )
                val actual = viewModel.state.reviewers

                assertEquals(expected, actual)
                assertEquals(false, viewModel.state.isLoading)
            }

        @Test
        fun `GIVEN reviewers with no review done WHEN init THEN list of reviewers is fetched`() =
            runTest {
                coEvery { repository.getReviews(any(), any(), any()) } returns Result.success(emptyList())
                viewModel = createViewModel()

                val expected = listOf(
                    Reviewer(2, "user2", false, 7, null),
                    Reviewer(3, "user3", false, 7, null),
                )
                val actual = viewModel.state.reviewers

                assertEquals(expected, actual)
            }

        @Test
        fun `GIVEN only reviewers who done reviews WHEN init THEN list of reviewers is fetched`() =
            runTest {
                coEvery { repository.getRequestedReviewers(any(), any(), any()) } returns Result.success(RequestedReviewersResponse(emptyList()))
                viewModel = createViewModel()

                val expected = listOf(Reviewer(1, "user1", true, 7, 5))
                val actual = viewModel.state.reviewers

                assertEquals(expected, actual)
            }

        @Test
        fun `WHEN there is an error during fetching data from 2 requests on init THEN error is shown`() =
            runTest {
                coEvery { repository.getReviews(any(), any(), any()) } returns Result.failure(WebException.NetworkError())
                coEvery { repository.getRequestedReviewers(any(), any(), any()) } returns Result.failure(WebException.NetworkError())
                viewModel = createViewModel()

                assertEquals(true, viewModel.state.isError)
                assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
                assertEquals(false, viewModel.state.isLoading)
            }

        @Test
        fun `WHEN there is an error during fetching data on init only from requested reviewers request THEN error is shown`() =
            runTest {
                coEvery { repository.getRequestedReviewers(any(), any(), any()) } returns Result.failure(WebException.NetworkError())
                viewModel = createViewModel()

                assertEquals(true, viewModel.state.isError)
                assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
                assertEquals(false, viewModel.state.isLoading)
            }

        @Test
        fun `WHEN there is an error during fetching data on init only from reviews request THEN error is shown`() =
            runTest {
                coEvery { repository.getReviews(any(), any(), any()) } returns Result.failure(WebException.NetworkError())
                viewModel = createViewModel()

                assertEquals(true, viewModel.state.isError)
                assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
                assertEquals(false, viewModel.state.isLoading)
            }
    }

    @Nested
    inner class OnActionTest {

        @Test
        fun `WHEN successful notify action THEN show success snackbar`() = runTest {
            viewModel = createViewModel()

            viewModel.onAction(ReviewersAction.Notify("user1"))

            assertEquals(ReviewersSnackbarType.SUCCESS, viewModel.state.snackbarTypeShown)
        }

        @Test
        fun `WHEN successful notify action THEN show loading indicator`() = runTest {
            viewModel = createViewModel()
            coEvery { repository.notify(any(), any(), any(), any()) } coAnswers { neverCompletingSuspension() }

            viewModel.onAction(ReviewersAction.Notify("user1"))

            assertTrue(
                viewModel.state.reviewers.first { it.login == "user1" }.isLoading,
            ) { "Clicked item should have loading indicator" }
            assertTrue(
                viewModel.state.reviewers.filterNot { it.login == "user1" }.none { it.isLoading },
            ) { "Only clicked item should have loading indicator" }
        }

        @Test
        fun `WHEN failed notify action THEN show failure snackbar`() = runTest {
            every { savedStateHandle.get<String>("pull_request_number") } returns "nonExistingPullRequestNumber"
            viewModel = createViewModel()

            viewModel.onAction(ReviewersAction.Notify("user1"))

            assertEquals(ReviewersSnackbarType.FAILURE, viewModel.state.snackbarTypeShown)
        }

        @Test
        fun `GIVEN user login WHEN on snackbar dismiss action THEN snackbar is not shown`() =
            runTest {
                viewModel = createViewModel()

                viewModel.onAction(ReviewersAction.Notify("user1"))
                viewModel.onAction(ReviewersAction.OnSnackbarDismiss)

                assertNull(viewModel.state.snackbarTypeShown)
            }

        @Test
        fun `GIVEN error state WHEN on try again action with success result THEN state has reviewers`() =
            runTest {
                coEvery {
                    repository.getReviews(any(), any(), any())
                } returns Result.failure(WebException.NetworkError())
                coEvery {
                    repository.getRequestedReviewers(any(), any(), any())
                } returns Result.failure(WebException.NetworkError())
                viewModel = createViewModel()

                clearMocks(repository)
                viewModel.onAction(ReviewersAction.OnTryAgain)

                val expected = listOf(
                    Reviewer(1, "user1", true, 7, 5),
                    Reviewer(2, "user2", false, 7, null),
                    Reviewer(3, "user3", false, 7, null),
                )
                val actual = viewModel.state.reviewers

                assertEquals(expected, actual)
                assertEquals(false, viewModel.state.isError)
                assertEquals(false, viewModel.state.isLoading)
            }

        @Test
        fun `GIVEN error state WHEN on try again action with failure result THEN error is shown`() =
            runTest {
                coEvery {
                    repository.getReviews(any(), any(), any())
                } returns Result.failure(WebException.NetworkError())
                coEvery {
                    repository.getRequestedReviewers(any(), any(), any())
                } returns Result.failure(WebException.NetworkError())
                viewModel = createViewModel()

                viewModel.onAction(ReviewersAction.OnTryAgain)

                assertEquals(true, viewModel.state.isError)
                assertEquals(false, viewModel.state.isLoading)
            }
    }
}
