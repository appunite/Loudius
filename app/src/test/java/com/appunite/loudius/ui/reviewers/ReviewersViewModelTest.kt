package com.appunite.loudius.ui.reviewers

import androidx.lifecycle.SavedStateHandle
import com.appunite.loudius.domain.PullRequestRepository
import com.appunite.loudius.domain.model.Reviewer
import com.appunite.loudius.fakes.FakePullRequestRepository
import com.appunite.loudius.util.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
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
        Clock.fixed(systemNow.toInstant(ZoneOffset.UTC), ZoneId.systemDefault())

    private val repository: PullRequestRepository = FakePullRequestRepository()
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true) {
        every { get<String>(any()) } returns "example"
        every { get<String>("submission_date") } returns "2022-01-29T08:00:00"
        every { get<String>("pull_request_number") } returns "correctPullRequestNumber"
    }

    private fun createViewModel() = ReviewersViewModel(repository, savedStateHandle)

    @BeforeEach
    fun setup() {
        mockkStatic(Clock::class)
        every { Clock.systemDefaultZone() } returns systemClockFixed
    }

    @Test
    fun `GIVEN no values in saved state WHEN init THEN throw IllegalStateException`() {
        every { savedStateHandle.get<String>(any()) } returns null

        assertThrows<java.lang.IllegalStateException> {
            createViewModel()
        }
    }

    @Test
    fun `GIVEN correct initial values WHEN init THEN state is correct`() {
        val viewModel = createViewModel()

        verify(exactly = 1) { savedStateHandle.get<String>("owner") }
        verify(exactly = 1) { savedStateHandle.get<String>("repo") }
        verify(exactly = 1) { savedStateHandle.get<String>("pull_request_number") }
        verify(exactly = 1) { savedStateHandle.get<String>("submission_date") }

        assertEquals("correctPullRequestNumber", viewModel.state.pullRequestNumber)
    }

    @Test
    fun `GIVEN empty reviewers source WHEN init THEN state is correct, no reviewers`() {
        every { savedStateHandle.get<String>("pull_request_number") } returns "pullRequestWithNoReviewers"

        val viewModel = createViewModel()

        assertEquals("pullRequestWithNoReviewers", viewModel.state.pullRequestNumber)
        assertEquals(emptyList<Reviewer>(), viewModel.state.reviewers)
    }

    @Test
    fun `GIVEN mix reviewers WHEN init THEN list of reviewers is fetched`() =
        runTest {
            val viewModel = createViewModel()

            val expected = listOf(
                Reviewer(1, "user1", true, 8, 6),
                Reviewer(2, "user2", true, 8, 6),
                Reviewer(3, "user3", false, 8, null),
                Reviewer(4, "user4", false, 8, null),
            )
            val actual = viewModel.state.reviewers

            assertTrue(
                actual.containsAll(expected) && expected.containsAll(actual),
            )
        }

    @Test
    fun `GIVEN reviewers with no review done WHEN init THEN list of reviewers is fetched`() =
        runTest {
            every { savedStateHandle.get<String>("pull_request_number") } returns "onlyRequestedReviewers"

            val viewModel = createViewModel()

            val expected = listOf(
                Reviewer(3, "user3", false, 8, null),
                Reviewer(4, "user4", false, 8, null),
            )
            val actual = viewModel.state.reviewers

            assertTrue(
                actual.containsAll(expected) && expected.containsAll(actual),
            )
        }

    @Test
    fun `GIVEN only reviewers who done reviews WHEN init THEN list of reviewers is fetched`() =
        runTest {
            every { savedStateHandle.get<String>("pull_request_number") } returns "onlyReviewsNumber"

            val viewModel = createViewModel()

            val expected = listOf(
                Reviewer(1, "user1", true, 8, 6),
                Reviewer(2, "user2", true, 8, 6),
            )
            val actual = viewModel.state.reviewers

            assertTrue(
                actual.containsAll(expected) && expected.containsAll(actual),
            )
        }
}
