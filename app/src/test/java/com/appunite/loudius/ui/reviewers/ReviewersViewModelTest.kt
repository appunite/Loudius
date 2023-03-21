package com.appunite.loudius.ui.reviewers

import androidx.lifecycle.SavedStateHandle
import com.appunite.loudius.domain.model.Reviewer
import com.appunite.loudius.fakes.FakePullRequestRepository
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.MainDispatcherExtension
import com.appunite.loudius.utils.neverCompletingSuspension
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class ReviewersViewModelTest {

    private val systemNow = LocalDateTime.parse("2022-01-29T15:00:00")
    private val systemClockFixed =
        Clock.fixed(systemNow.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"))

    private val repository: FakePullRequestRepository = FakePullRequestRepository()
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

            assertThrows<java.lang.IllegalStateException> {
                createViewModel()
            }
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
            repository.setReviewsAnswer { neverCompletingSuspension() }
            viewModel = createViewModel()

            assertEquals(true, viewModel.state.isLoading)
            assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
        }

        @Test
        fun `GIVEN no reviewers WHEN init THEN state is correct with no reviewers`() {
            repository.setReviewsAnswer { Result.success(emptyList()) }
            repository.setRequestedReviewersAnswer {
                Result.success(RequestedReviewersResponse(emptyList()))
            }

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
                    Reviewer(2, "user2", true, 7, 5),
                    Reviewer(3, "user3", false, 7, null),
                    Reviewer(4, "user4", false, 7, null),
                )
                val actual = viewModel.state.reviewers

                assertEquals(expected, actual)
                assertEquals(false, viewModel.state.isLoading)
            }

        @Test
        fun `GIVEN reviewers with no review done WHEN init THEN list of reviewers is fetched`() =
            runTest {
                repository.setReviewsAnswer { Result.success(emptyList()) }
                viewModel = createViewModel()

                val expected = listOf(
                    Reviewer(3, "user3", false, 7, null),
                    Reviewer(4, "user4", false, 7, null),
                )
                val actual = viewModel.state.reviewers

                assertEquals(expected, actual)
            }

        @Test
        fun `GIVEN only reviewers who done reviews WHEN init THEN list of reviewers is fetched`() =
            runTest {
                repository.setRequestedReviewersAnswer {
                    Result.success(RequestedReviewersResponse(emptyList()))
                }
                viewModel = createViewModel()

                val expected = listOf(
                    Reviewer(1, "user1", true, 7, 5),
                    Reviewer(2, "user2", true, 7, 5),
                )
                val actual = viewModel.state.reviewers

                assertEquals(expected, actual)
            }

        @Test
        fun `WHEN there is an error during fetching data from 2 requests on init THEN error is shown`() =
            runTest {
                repository.setReviewsAnswer { Result.failure(WebException.NetworkError()) }
                repository.setRequestedReviewersAnswer { Result.failure(WebException.NetworkError()) }
                viewModel = createViewModel()

                assertEquals(true, viewModel.state.isError)
                assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
                assertEquals(false, viewModel.state.isLoading)
            }

        @Test
        fun `WHEN there is an error during fetching data on init only from requested reviewers request THEN error is shown`() =
            runTest {
                repository.setRequestedReviewersAnswer { Result.failure(WebException.NetworkError()) }
                viewModel = createViewModel()

                assertEquals(true, viewModel.state.isError)
                assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
                assertEquals(false, viewModel.state.isLoading)
            }

        @Test
        fun `WHEN there is an error during fetching data on init only from reviews request THEN error is shown`() =
            runTest {
                repository.setReviewsAnswer { Result.failure(WebException.NetworkError()) }
                viewModel = createViewModel()

                assertEquals(true, viewModel.state.isError)
                assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
                assertEquals(false, viewModel.state.isLoading)
            }
    }

    @Nested
    inner class OnActionTest {

        @Test
        fun `GIVEN user login WHEN Notify action THEN show snackbar`() = runTest {
            viewModel = createViewModel()

            viewModel.onAction(ReviewersAction.Notify("ExampleUser"))

            assertEquals(true, viewModel.state.isSuccessSnackbarShown)
        }

        @Test
        fun `GIVEN user login WHEN on snackbar dismiss action THEN snackbar is not shown`() =
            runTest {
                viewModel = createViewModel()

                viewModel.onAction(ReviewersAction.Notify("ExampleUser"))
                viewModel.onAction(ReviewersAction.OnSnackbarDismiss)

                assertEquals(false, viewModel.state.isSuccessSnackbarShown)
            }

        @Test
        fun `GIVEN error state WHEN on try again action with success result THEN state has reviewers`() =
            runTest {
                repository.setReviewsAnswer { Result.failure(WebException.NetworkError()) }
                repository.setRequestedReviewersAnswer { Result.failure(WebException.NetworkError()) }
                viewModel = createViewModel()

                repository.resetReviewsAnswer()
                repository.resetRequestedReviewersAnswer()
                viewModel.onAction(ReviewersAction.OnTryAgain)

                val expected = listOf(
                    Reviewer(1, "user1", true, 7, 5),
                    Reviewer(2, "user2", true, 7, 5),
                    Reviewer(3, "user3", false, 7, null),
                    Reviewer(4, "user4", false, 7, null),
                )
                val actual = viewModel.state.reviewers

                assertEquals(expected, actual)
                assertEquals(false, viewModel.state.isError)
                assertEquals(false, viewModel.state.isLoading)
            }

        @Test
        fun `GIVEN error state WHEN on try again action with failure result THEN error is shown`() =
            runTest {
                repository.setReviewsAnswer { Result.failure(WebException.NetworkError()) }
                repository.setRequestedReviewersAnswer { Result.failure(WebException.NetworkError()) }
                viewModel = createViewModel()

                viewModel.onAction(ReviewersAction.OnTryAgain)

                assertEquals(true, viewModel.state.isError)
                assertEquals(false, viewModel.state.isLoading)
            }
    }
}
