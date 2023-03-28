package com.appunite.loudius.domain

import com.appunite.loudius.domain.repository.PullRequestRepositoryImpl
import com.appunite.loudius.fakes.FakePullRequestDataSource
import com.appunite.loudius.network.datasource.UserDataSource
import com.appunite.loudius.network.model.RequestedReviewer
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState
import com.appunite.loudius.network.model.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class PullRequestRepositoryImpTest {

    private val pullRequestDataSource = FakePullRequestDataSource()
    private val userDataSource: UserDataSource = mockk {
        coEvery { getUser() } returns Result.success(User(1, "user1"))
    }
    private val repository = PullRequestRepositoryImpl(pullRequestDataSource, userDataSource)

    @Nested
    inner class GetReviewsFunctionTest {

        @Test
        fun `GIVEN correct values WHEN get reviews THEN return result with reviews excluding ones from current user`() =
            runTest {
                val actual = repository.getReviews("example", "example", "correctPullRequestNumber")

                val date1 = LocalDateTime.parse("2022-01-29T10:00:00")

                val expected = Result.success(
                    listOf(
                        Review("4", User(2, "user2"), ReviewState.COMMENTED, date1),
                        Review("5", User(2, "user2"), ReviewState.COMMENTED, date1),
                        Review("6", User(2, "user2"), ReviewState.APPROVED, date1),
                    ),
                )

                assertEquals(expected, actual)
            }

        // TODO: Write tests with failure cases
    }

    @Nested
    inner class GetRequestedReviewersTest {
        @Test
        fun `GIVEN correct values WHEN get requested reviewers THEN return result with requested reviewers`() =
            runTest {
                val actual = repository.getRequestedReviewers(
                    "example",
                    "example",
                    "correctPullRequestNumber",
                )

                val expected = Result.success(
                    RequestedReviewersResponse(
                        listOf(
                            RequestedReviewer(3, "user3"),
                            RequestedReviewer(4, "user4"),
                        ),
                    ),
                )

                assertEquals(expected, actual)
            }
        // TODO: Write tests with failure cases
    }

    @Nested
    inner class NotifyTest {

        @Test
        fun `GIVEN correct values WHEN notify THEN return success result`() = runTest {
            val actual = repository.notify(
                "exampleOwner",
                "exampleRepo",
                "correctPullRequestNumber",
                "@ExampleUser",
            )

            val expected = Result.success(Unit)

            assertEquals(expected, actual)
        }
        // TODO: Write tests with failure cases
    }
}
