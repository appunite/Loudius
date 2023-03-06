package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState
import com.appunite.loudius.network.model.Reviewer
import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.retrofitTestDouble
import com.appunite.loudius.network.services.GithubPullRequestsService
import com.appunite.loudius.network.utils.WebException
import java.time.LocalDateTime
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
    inner class GetReviewersRequestTest {

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

    @Nested
    inner class GetReviewsRequestTest {

        @Test
        fun `Given request WHEN connectivity problem occurred THEN return failure with Network error`() =
            runTest {
                mockWebServer.enqueue(
                    MockResponse()
                        .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY)
                )

                val actualResponse = pullRequestDataSource.getReviews(
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
                    [
    {
        "id": 1,
        "node_id": "exampleId",
        "user": {
            "login": "exampleUser",
            "id": 33498031,
            "node_id": "exampleNodeId",
            "avatar_url": "https://avatars.githubusercontent.com/u/33498031?v=4",
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
        },
        "body": "",
        "state": "COMMENTED",
        "html_url": "https://github.com/exampleOwner/exampleRepo/pull/20#pullrequestreview-1321494756",
        "pull_request_url": "https://api.github.com/repos/exampleOwner/exampleRepo/pulls/20",
        "author_association": "COLLABORATOR",
        "_links": {
            "html": {
                "href": "https://github.com/exampleOwner/exampleRepo/pull/20#pullrequestreview-1321494756"
            },
            "pull_request": {
                "href": "https://api.github.com/repos/exampleOwner/exampleRepo/pulls/20"
            }
        },
        "submitted_at": "2023-03-02T10:21:36Z",
        "commit_id": "exampleCommitId"
    }]
                """.trimIndent()
                mockWebServer.enqueue(
                    MockResponse()
                        .setResponseCode(200)
                        .setBody(jsonResponse)
                )

                val actualResponse = pullRequestDataSource.getReviews(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                    "validAccessToken"
                )

                val expected = Result.success(
                    listOf(
                        Review(
                            "1",
                            User(33498031),
                            ReviewState.COMMENTED,
                            LocalDateTime.parse("2023-03-02T10:21:36")
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

                val actualResponse = pullRequestDataSource.getReviews(
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
