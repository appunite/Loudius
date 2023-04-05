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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isSuccess

@OptIn(ExperimentalCoroutinesApi::class)
class PullRequestRepositoryImpTest {

    private val pullRequestDataSource = spyk(FakePullRequestDataSource())
    private val userDataSource: UserDataSource = mockk {
        coEvery { getUser() } returns Result.success(Defaults.currentUser())
    }
    private val repository = PullRequestRepositoryImpl(pullRequestDataSource, userDataSource)

    @Nested
    inner class GetReviewsFunctionTest {

        @Test
        fun `GIVEN correct pull request data WHEN getting reviews THEN return result with reviews excluding ones from current user`() =
            runTest {
                val pullRequestNumber = "correctPullRequestNumber"

                val response = repository.getReviews("example", "example", pullRequestNumber)

                expectThat(response)
                    .isSuccess()
                    .containsExactly(
                        Review("4", User(1, "user1"), ReviewState.COMMENTED, Defaults.date1),
                        Review("5", User(1, "user1"), ReviewState.COMMENTED, Defaults.date2),
                        Review("6", User(1, "user1"), ReviewState.APPROVED, Defaults.date3),
                        )
            }

        @Test
        fun `GIVEN incorrect pull request number WHEN getting reviews THEN return Unknown Error with 404 code`() =
            runTest {
                val pullRequestNumber = "incorrectPullRequestNumber"

                val response = repository.getReviews("example", "example", pullRequestNumber)

                expectThat(response)
                    .isFailure()
                    .isEqualTo(WebException.UnknownError(404, null))
            }
    }

    @Nested
    inner class GetRequestedReviewersTest {
        @Test
        fun `GIVEN correct pull request number WHEN get requesting reviewers THEN return result with requested reviewers`() =
            runTest {
                val pullRequestNumber = "correctPullRequestNumber"

                val result = repository.getRequestedReviewers(
                    "example",
                    "example",
                    pullRequestNumber,
                )

                expectThat(result).isSuccess().isEqualTo(
                    RequestedReviewersResponse(
                        listOf(
                            RequestedReviewer(3, "user3"),
                            RequestedReviewer(4, "user4"),
                        ),
                    )
                )
            }

        @Test
        fun `GIVEN incorrect pull request number WHEN get requested reviewers THEN return Unknown Error with 404 code`() =
            runTest {
                val pullRequestNumber = "incorrectPullRequestNumber"

                val response = repository.getRequestedReviewers(
                    "example",
                    "example",
                    pullRequestNumber,
                )

                expectThat(response)
                    .isFailure()
                    .isEqualTo(WebException.UnknownError(404, null))
            }
    }

    @Nested
    inner class NotifyTest {

        @Test
        fun `GIVEN correct pull request number WHEN notifying THEN return success result`() = runTest {
            val pullRequestNumber = "correctPullRequestNumber"

            val result = repository.notify(
                "exampleOwner",
                "exampleRepo",
                pullRequestNumber,
                "@ExampleUser",
            )

            expectThat(result)
                .isSuccess()
        }

        @Test
        fun `GIVEN incorrect pull request number WHEN notifying THEN return Unknown Error with 404 code`() = runTest {
            coEvery {
                repository.notify(any(), any(), any(), any())
            } returns Result.failure(WebException.UnknownError(404, null))
            val pullRequestNumber = "incorrectPullRequestNumber"

            val response = repository.notify(
                "exampleOwner",
                "exampleRepo",
                pullRequestNumber,
                "@ExampleUser",
            )

            expectThat(response)
                .isFailure()
                .isEqualTo(WebException.UnknownError(404, null))
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
                        "correctAuthor",
                    ),
                )

                val response = repository.getCurrentUserPullRequests()

                expectThat(response)
                    .isSuccess()
                    .isEqualTo(Defaults.pullRequestsResponse())
            }

        @Test
        fun `WHEN Network Error is returned during fetching user's pull request THEN return Network Error`() =
            runTest {
                coEvery {
                    pullRequestDataSource.getPullRequestsForUser(any())
                } returns Result.failure(WebException.NetworkError())

                val response = repository.getCurrentUserPullRequests()


                expectThat(response)
                    .isFailure()
                    .isEqualTo(WebException.NetworkError())
            }
    }
}
