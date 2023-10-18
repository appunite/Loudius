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
import com.appunite.loudius.util.neverCompletingSuspension
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.containsExactly
import strikt.assertions.filterNot
import strikt.assertions.first
import strikt.assertions.isA
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import strikt.assertions.isTrue

@ExtendWith(MainDispatcherExtension::class)
class ReviewersViewModelTest {

    private val systemNow = LocalDateTime.parse("2022-01-29T15:00:00")
    private val systemClockFixed = systemNow.toInstant(TimeZone.UTC)

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
        mockkObject(Clock.System)
        every { Clock.System.now() } returns systemClockFixed
    }

    @Nested
    inner class InitTest {
        @Test
        fun `WHEN refresh data THEN refresh data and display reviewers`() = runTest {
            viewModel = createViewModel()

            viewModel.refreshData()

            expectThat(viewModel.state.data).isA<Data.Success>().and {
                get(Data.Success::reviewers).containsExactly(
                    Reviewer(1, "user1", true, 7, 5),
                    Reviewer(2, "user2", false, 7, null),
                    Reviewer(3, "user3", false, 7, null),
                )
            }
        }

        @Test
        fun `WHEN refresh data THEN start refreshing data and set isRefreshing to true`() = runTest {
            viewModel = createViewModel()

            coEvery {
                repository.getRequestedReviewers(
                    any(),
                    any(),
                    any(),
                )
            } coAnswers { neverCompletingSuspension() }

            coEvery {
                repository.getReviews(
                    any(),
                    any(),
                    any(),
                )
            } coAnswers { neverCompletingSuspension() }

            viewModel.refreshData()

            expectThat(viewModel.isRefreshing.value).isTrue()
        }

        @Test
        fun `WHEN refresh data THEN refresh data and set isRefreshing to false`() = runTest {
            viewModel = createViewModel()

            viewModel.refreshData()

            expectThat(viewModel.isRefreshing.value).isFalse()
        }

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

            expectThat(viewModel.state)
                .get(ReviewersState::pullRequestNumber)
                .isEqualTo("correctPullRequestNumber")
        }

        @Test
        fun `GIVEN correct initial values WHEN init starts THEN state is loading`() = runTest {
            coEvery {
                repository.getReviews(
                    any(),
                    any(),
                    any(),
                )
            } coAnswers { neverCompletingSuspension() }
            viewModel = createViewModel()

            expectThat(viewModel.state.data).isA<Data.Loading>()
        }

        @Test
        fun `GIVEN no reviewers WHEN init THEN state is correct with no reviewers`() {
            coEvery {
                repository.getReviews(
                    any(),
                    any(),
                    any(),
                )
            } returns Result.success(emptyList())
            coEvery {
                repository.getRequestedReviewers(
                    any(),
                    any(),
                    any(),
                )
            } returns Result.success(RequestedReviewersResponse(emptyList()))

            viewModel = createViewModel()

            expectThat(viewModel.state.data).isA<Data.Success>().and {
                get(Data.Success::reviewers).isEmpty()
            }
        }

        @Test
        fun `GIVEN mixed reviewers WHEN init THEN list of reviewers is fetched`() =
            runTest {
                viewModel = createViewModel()

                expectThat(viewModel.state.data).isA<Data.Success>().and {
                    get(Data.Success::reviewers).containsExactly(
                        Reviewer(1, "user1", true, 7, 5),
                        Reviewer(2, "user2", false, 7, null),
                        Reviewer(3, "user3", false, 7, null),
                    )
                }
            }

        @Test
        fun `GIVEN reviewers with no review done WHEN init THEN list of reviewers is fetched`() =
            runTest {
                coEvery { repository.getReviews(any(), any(), any()) } returns Result.success(
                    emptyList(),
                )
                viewModel = createViewModel()

                expectThat(viewModel.state.data).isA<Data.Success>().and {
                    get(Data.Success::reviewers)
                        .containsExactly(
                            Reviewer(2, "user2", false, 7, null),
                            Reviewer(3, "user3", false, 7, null),
                        )
                }
            }

        @Test
        fun `GIVEN only reviewers who done reviews WHEN init THEN list of reviewers is fetched`() =
            runTest {
                coEvery {
                    repository.getRequestedReviewers(
                        any(),
                        any(),
                        any(),
                    )
                } returns Result.success(RequestedReviewersResponse(emptyList()))
                viewModel = createViewModel()

                expectThat(viewModel.state.data).isA<Data.Success>().and {
                    get(Data.Success::reviewers).containsExactly(
                        Reviewer(1, "user1", true, 7, 5),
                    )
                }
            }

        @Test
        fun `WHEN there is an error during fetching data from 2 requests on init THEN error is shown`() =
            runTest {
                coEvery { repository.getReviews(any(), any(), any()) } returns Result.failure(
                    WebException.NetworkError(),
                )
                coEvery {
                    repository.getRequestedReviewers(
                        any(),
                        any(),
                        any(),
                    )
                } returns Result.failure(WebException.NetworkError())
                viewModel = createViewModel()

                expectThat(viewModel.state.data).isA<Data.Error>()
            }

        @Test
        fun `WHEN there is an error during fetching data on init only from requested reviewers request THEN error is shown`() =
            runTest {
                coEvery {
                    repository.getRequestedReviewers(
                        any(),
                        any(),
                        any(),
                    )
                } returns Result.failure(WebException.NetworkError())
                viewModel = createViewModel()

                expectThat(viewModel.state.data).isA<Data.Error>()
            }

        @Test
        fun `WHEN there is an error during fetching data on init only from reviews request THEN error is shown`() =
            runTest {
                coEvery { repository.getReviews(any(), any(), any()) } returns Result.failure(
                    WebException.NetworkError(),
                )
                viewModel = createViewModel()

                expectThat(viewModel.state.data).isA<Data.Error>()
            }
    }

    @Nested
    inner class OnActionTest {

        @Test
        fun `WHEN successful notify action THEN show success snackbar`() = runTest {
            viewModel = createViewModel()

            viewModel.onAction(ReviewersAction.Notify("user1"))

            expectThat(viewModel.state)
                .get(ReviewersState::snackbarTypeShown)
                .isEqualTo(ReviewersSnackbarType.SUCCESS)
        }

        @Test
        fun `WHEN successful notify action THEN show loading indicator`() = runTest {
            viewModel = createViewModel()
            coEvery {
                repository.notify(
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } coAnswers { neverCompletingSuspension() }

            viewModel.onAction(ReviewersAction.Notify("user1"))

            expectThat(viewModel.state.data).isA<Data.Success>().and {
                // Clicked item should have loading indicator
                get(Data.Success::reviewers)
                    .first { it.login == "user1" }
                    .get(Reviewer::isLoading)
                    .isTrue()

                // Other items should NOT have loading indicator
                get(Data.Success::reviewers)
                    .filterNot { it.login == "user1" }
                    .all { get(Reviewer::isLoading).isFalse() }
            }
        }

        @Test
        fun `WHEN failed notify action THEN show failure snackbar`() = runTest {
            every { savedStateHandle.get<String>("pull_request_number") } returns "nonExistingPullRequestNumber"
            viewModel = createViewModel()

            expectThat(viewModel.state)
                .get(ReviewersState::snackbarTypeShown)
                .isNull()

            viewModel.onAction(ReviewersAction.Notify("user1"))

            expectThat(viewModel.state)
                .get(ReviewersState::snackbarTypeShown)
                .isEqualTo(ReviewersSnackbarType.FAILURE)
        }

        @Test
        fun `GIVEN user login WHEN on snackbar dismiss action THEN snackbar is not shown`() =
            runTest {
                viewModel = createViewModel()

                viewModel.onAction(ReviewersAction.Notify("user1"))
                viewModel.onAction(ReviewersAction.OnSnackbarDismiss)

                expectThat(viewModel.state)
                    .get(ReviewersState::snackbarTypeShown)
                    .isNull()
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

                expectThat(viewModel.state.data).isA<Data.Success>().and {
                    get(Data.Success::reviewers).containsExactly(
                        Reviewer(1, "user1", true, 7, 5),
                        Reviewer(2, "user2", false, 7, null),
                        Reviewer(3, "user3", false, 7, null),
                    )
                }
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

                expectThat(viewModel.state.data).isA<Data.Error>()
            }
    }
}
