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

package com.appunite.loudius.domain

import com.appunite.loudius.domain.repository.PullRequestRepositoryImpl
import com.appunite.loudius.fakes.FakePullRequestDataSource
import com.appunite.loudius.network.datasource.UserDataSource
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewer
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState
import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.Defaults
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import java.time.LocalDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PullRequestRepositoryImpTest {

    private val pullRequestDataSource = spyk(FakePullRequestDataSource())
    private val userDataSource: UserDataSource = mockk {
        coEvery { getUser() } returns Result.success(User(1, "user1"))
    }
    private val repository = PullRequestRepositoryImpl(pullRequestDataSource, userDataSource)

    @Nested
    inner class GetReviewsFunctionTest {

        @Test
        fun `GIVEN correct pull request data WHEN getting reviews THEN return result with reviews excluding ones from current user`() =
            runTest {
                val pullRequestNumber = "correctPullRequestNumber"

                val actual = repository.getReviews("example", "example", pullRequestNumber)

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

        @Test
        fun `GIVEN incorrect pull request number WHEN getting reviews THEN return Unknown Error with 404 code`() =
            runTest {
                val pullRequestNumber = "incorrectPullRequestNumber"

                val actual = repository.getReviews("example", "example", pullRequestNumber)

                assertEquals(
                    Result.failure<List<Review>>(WebException.UnknownError(404, null)),
                    actual
                )
            }
    }

    @Nested
    inner class GetRequestedReviewersTest {
        @Test
        fun `GIVEN correct pull request number WHEN get requesting reviewers THEN return result with requested reviewers`() =
            runTest {
                val pullRequestNumber = "correctPullRequestNumber"

                val actual = repository.getRequestedReviewers(
                    "example",
                    "example",
                    pullRequestNumber,
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

        @Test
        fun `GIVEN incorrect pull request number WHEN get requested reviewers THEN return Unknown Error with 404 code`() =
            runTest {
                val pullRequestNumber = "incorrectPullRequestNumber"

                val actual = repository.getRequestedReviewers(
                    "example",
                    "example",
                    pullRequestNumber,
                )

                assertEquals(
                    Result.failure<List<RequestedReviewersResponse>>(
                        WebException.UnknownError(404, null)
                    ),
                    actual
                )
            }
    }

    @Nested
    inner class NotifyTest {

        @Test
        fun `GIVEN correct pull request number WHEN notifying THEN return success result`() = runTest {
            val pullRequestNumber = "correctPullRequestNumber"

            val actual = repository.notify(
                "exampleOwner",
                "exampleRepo",
                pullRequestNumber,
                "@ExampleUser",
            )

            assertEquals(Result.success(Unit), actual)
        }

        @Test
        fun `GIVEN incorrect pull request number WHEN notifying THEN return success result`() = runTest {
            coEvery {
                repository.notify(any(), any(), any(), any())
            } returns Result.failure(WebException.UnknownError(404, null))
            val pullRequestNumber = "incorrectPullRequestNumber"

            val actual = repository.notify(
                "exampleOwner",
                "exampleRepo",
                pullRequestNumber,
                "@ExampleUser",
            )

            assertEquals(
                Result.failure<Unit>(WebException.UnknownError(404, null)),
                actual
            )
        }
    }

    @Nested
    inner class GetPullRequestsForUserTest {
        @Test
        fun `GIVEN logged in user WHEN getting user's pull requests THEN return pull requests`() =
            runTest {
                coEvery { userDataSource.getUser() } returns Result.success(
                    User(
                        0,
                        "correctAuthor"
                    )
                )

                val actual = repository.getCurrentUserPullRequests()

                assertEquals(Result.success(Defaults.pullRequestsResponse()), actual)
            }

        @Test
        fun `WHEN Network Error is returned during fetching user's pull request THEN return Network Error`() =
            runTest {
                coEvery {
                    pullRequestDataSource.getPullRequestsForUser(any())
                } returns Result.failure(WebException.NetworkError())

                val actual = repository.getCurrentUserPullRequests()

                assertEquals(
                    Result.failure<PullRequestsResponse>(WebException.NetworkError()),
                    actual
                )
            }
    }

}
