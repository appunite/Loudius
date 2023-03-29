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

package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.retrofitTestDouble
import com.appunite.loudius.network.services.UserService
import com.appunite.loudius.network.utils.WebException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class UserDataSourceTest {

    private val mockWebServer: MockWebServer = MockWebServer()
    private val userService =
        retrofitTestDouble(mockWebServer = mockWebServer).create(UserService::class.java)
    private val userDataSource = UserDataSourceImpl(userService)

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `Given request WHEN connectivity problem occurred THEN return failure with Network error`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY),
            )

            val actualResponse = userDataSource.getUser()
            Assertions.assertInstanceOf(
                WebException.NetworkError::class.java,
                actualResponse.exceptionOrNull(),
            ) { "Exception thrown should be NetworkError type" }
        }

    @Test
    fun `Given correct params WHEN successful response THEN return success`() = runTest {
        //language=JSON
        val jsonResponse = """
            {
                "login": "exampleUser",
                "id": 1,
                "node_id": "MDQ6VXNlcjE4MTAyNzc1",
                "avatar_url": "https://avatars.githubusercontent.com/u/18102775?v=4",
                "gravatar_id": "",
                "url": "https://api.github.com/users/exampleUser",
                "html_url": "https://github.com/exampleUser",
                "followers_url": "https://api.github.com/users/exampleUser/followers",
                "following_url": "https://api.github.com/users/exampleUser/following{/other_user}",
                "gists_url": "https://api.github.com/users/exampleUser/gists{/gist_id}",
                "starred_url": "https://api.github.com/users/exampleUser/starred{/owner}{/repo}",
                "subscriptions_url": "https://api.github.com/users/exampleUser/subscriptions",
                "organizations_url": "https://api.github.com/users/exampleUser/orgs",
                "repos_url": "https://api.github.com/users/exampleUser/repos",
                "events_url": "https://api.github.com/users/exampleUser/events{/privacy}",
                "received_events_url": "https://api.github.com/users/exampleUser/received_events",
                "type": "User",
                "site_admin": false,
                "name": "Name Surname",
                "company": null,
                "blog": "",
                "location": null,
                "email": null,
                "hireable": null,
                "bio": "bio description",
                "twitter_username": null,
                "public_repos": 8,
                "public_gists": 2,
                "followers": 16,
                "following": 14,
                "created_at": "2016-03-27T17:03:48Z",
                "updated_at": "2023-02-15T13:22:50Z"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(jsonResponse),
        )

        val actualResponse = userDataSource.getUser()

        val expected = Result.success(
            User(
                id = 1,
                login = "exampleUser",
            ),
        )

        Assertions.assertEquals(expected, actualResponse) { "Data should be valid" }
    }

    @Test
    fun `Given incorrect access token WHEN processing request THEN return failure with Unknown error`() =
        runTest {
            // language=JSON
            val jsonResponse = """
                    {
                        "message": "Bad credentials",
                        "documentation_url": "https://docs.github.com/rest"
                    }
            """.trimIndent()

            mockWebServer.enqueue(
                MockResponse().setResponseCode(401).setBody(jsonResponse),
            )

            val actualResponse = userDataSource.getUser()

            val expected = Result.failure<RequestedReviewersResponse>(
                WebException.UnknownError(
                    401,
                    "Bad credentials",
                ),
            )

            Assertions.assertEquals(expected, actualResponse) { "Data should be valid" }
        }
}
