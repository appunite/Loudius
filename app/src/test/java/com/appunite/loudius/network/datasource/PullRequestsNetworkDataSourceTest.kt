package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Reviewer
import com.appunite.loudius.network.retrofitTestDouble
import com.appunite.loudius.network.services.GithubPullRequestsService
import com.appunite.loudius.network.utils.WebException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class PullRequestsNetworkDataSourceTest {

    private val mockWebServer: MockWebServer = MockWebServer().apply { start(8080) }
    private val userApi =
        retrofitTestDouble(mockWebServer = mockWebServer).create(GithubPullRequestsService::class.java)
    private val pullRequestDataSource = PullRequestsNetworkDataSource(userApi)


    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Nested
    inner class GetReviewersRequestsTest {

        @Test
        fun `Given request WHEN connectivity problem occurred THEN return failure with Network error`() =
            runTest {
                mockWebServer.enqueue(
                    MockResponse()
                        .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY)
                )

                val actualResponse = pullRequestDataSource.getReviewers(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                    "validAccessToken"
                )
                Assertions.assertInstanceOf(
                    WebException.NetworkError::class.java,
                    actualResponse.exceptionOrNull()
                )
            }

        @Test
        fun `Given correct params WHEN successful response THEN return success`() =
            runTest {
                //language=JSON
                val jsonResponse = """
                {
                    "users": [
                        {
                            "login": "exampleLogin",
                            "id": 1,
                            "node_id": "example_node_id",
                            "avatar_url": "https://example/avatar",
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
                            "site_admin": false
                        }
                    ],
                    "teams": []
                }
            """.trimIndent()

                mockWebServer.enqueue(
                    MockResponse()
                        .setResponseCode(200)
                        .setBody(jsonResponse)
                )

                val actualResponse = pullRequestDataSource.getReviewers(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                    "validAccessToken"
                )

                val expected = Result.success(
                    RequestedReviewersResponse(
                        listOf(
                            Reviewer(
                                "1",
                                "exampleLogin",
                                "https://example/avatar"
                            )
                        )
                    )
                )

                assertEquals(expected, actualResponse) { "Data should be correct" }
            }


        @Test
        fun `Given incorrect access token WHEN processing request THEN return failure with Network error`() =
            runTest {
                // language=JSON
                val jsonResponse = """
                    {
                        "message": "Bad credentials",
                        "documentation_url": "https://docs.github.com/rest"
                    }
                """.trimIndent()

                mockWebServer.enqueue(
                    MockResponse()
                        .setResponseCode(401)
                        .setBody(jsonResponse)
                )

                val actualResponse = pullRequestDataSource.getReviewers(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                    "validAccessToken"
                )

                val expected = Result.failure<RequestedReviewersResponse>(
                    WebException.UnknownError(
                        401,
                        "Bad credentials"
                    )
                )


                assertEquals(expected, actualResponse)
            }


    }


}
