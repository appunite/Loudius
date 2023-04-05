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
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo
import strikt.assertions.isSuccess
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
                val result = repository.getReviews("example", "example", "correctPullRequestNumber")

                expectThat(result)
                    .isSuccess()
                    .containsExactly(
                        Review(
                            "4",
                            User(2, "user2"),
                            ReviewState.COMMENTED,
                            LocalDateTime.parse("2022-01-29T10:00:00")
                        ),
                        Review(
                            "5",
                            User(2, "user2"),
                            ReviewState.COMMENTED,
                            LocalDateTime.parse("2022-01-29T10:00:00")
                        ),
                        Review(
                            "6",
                            User(2, "user2"),
                            ReviewState.APPROVED,
                            LocalDateTime.parse("2022-01-29T10:00:00")
                        ),
                    )
            }

        // TODO: Write tests with failure cases
    }

    @Nested
    inner class GetRequestedReviewersTest {
        @Test
        fun `GIVEN correct values WHEN get requested reviewers THEN return result with requested reviewers`() =
            runTest {
                val result = repository.getRequestedReviewers(
                    "example",
                    "example",
                    "correctPullRequestNumber",
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
        // TODO: Write tests with failure cases
    }

    @Nested
    inner class NotifyTest {

        @Test
        fun `GIVEN correct values WHEN notify THEN return success result`() = runTest {
            val result = repository.notify(
                "exampleOwner",
                "exampleRepo",
                "correctPullRequestNumber",
                "@ExampleUser",
            )

            expectThat(result).isSuccess()
        }
        // TODO: Write tests with failure cases
    }
}
