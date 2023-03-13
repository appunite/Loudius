package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewer
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState
import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.retrofitTestDouble
import com.appunite.loudius.network.services.PullRequestsService
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
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class PullRequestsNetworkDataSourceTest {

    private val mockWebServer: MockWebServer = MockWebServer()
    private val pullRequestsService =
        retrofitTestDouble(mockWebServer = mockWebServer).create(PullRequestsService::class.java)
    private val pullRequestDataSource = PullRequestsNetworkDataSource(pullRequestsService)

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Nested
    inner class GetPullRequestsForUserTest {
        @Test
        fun `Given request WHEN connectivity problem occurred THEN return failure with Network error`() =
            runTest {
                mockWebServer.enqueue(
                    MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY),
                )

                val actualResponse = pullRequestDataSource.getPullRequestsForUser(
                    "exampleUser",
                )
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
                    "total_count":1,
                    "incomplete_results":false,
                    "items":[
                        {
                            "url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1",
                            "repository_url":"https://api.github.com/repos/exampleOwner/exampleRepo",
                            "labels_url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/labels{/name}",
                            "comments_url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/comments",
                            "events_url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/events",
                            "html_url":"https://github.com/exampleOwner/exampleRepo/pull/1",
                            "id":1,
                            "node_id":"example_node_id",
                            "number":1,
                            "title":"example title",
                            "user":{
                                "login":"exampleUser",
                                "id":1,
                                "node_id":"example_user_node_id",
                                "avatar_url":"https://avatars.githubusercontent.com/u/1",
                                "gravatar_id":"",
                                "url":"https://api.github.com/users/exampleUser",
                                "html_url":"https://github.com/exampleUser",
                                "followers_url":"https://api.github.com/users/exampleUser/followers",
                                "following_url":"https://api.github.com/users/exampleUser/following{/other_user}",
                                "gists_url":"https://api.github.com/users/exampleUser/gists{/gist_id}",
                                "starred_url":"https://api.github.com/users/exampleUser/starred{/owner}{/repo}",
                                "subscriptions_url":"https://api.github.com/users/exampleUser/subscriptions",
                                "organizations_url":"https://api.github.com/users/exampleUser/orgs",
                                "repos_url":"https://api.github.com/users/exampleUser/repos",
                                "events_url":"https://api.github.com/users/exampleUser/events{/privacy}",
                                "received_events_url":"https://api.github.com/users/exampleUser/received_events",
                                "type":"User",
                                "site_admin":false
                            },
                            "labels":[
                                
                            ],
                            "state":"open",
                            "locked":false,
                            "assignee":null,
                            "assignees":[
                                
                            ],
                            "milestone":null,
                            "comments":1,
                            "created_at":"2023-03-07T09:21:45Z",
                            "updated_at":"2023-03-07T09:24:24Z",
                            "closed_at":null,
                            "author_association":"COLLABORATOR",
                            "active_lock_reason":null,
                            "draft":false,
                            "pull_request":{
                                "url":"https://api.github.com/repos/exampleOwner/exampleRepo/pulls/1",
                                "html_url":"https://github.com/exampleOwner/exampleRepo/pull/1",
                                "diff_url":"https://github.com/exampleOwner/exampleRepo/pull/1.diff",
                                "patch_url":"https://github.com/exampleOwner/exampleRepo/pull/1.patch",
                                "merged_at":null
                            },
                            "body":"pr only for demonstration purposes  . . . .",
                            "reactions":{
                                "url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/reactions",
                                "total_count":0,
                                "+1":0,
                                "-1":0,
                                "laugh":0,
                                "hooray":0,
                                "confused":0,
                                "heart":0,
                                "rocket":0,
                                "eyes":0
                            },
                            "timeline_url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/timeline",
                            "performed_via_github_app":null,
                            "state_reason":null,
                            "score":1.0
                        }
                    ]
                }
            """.trimIndent()

            mockWebServer.enqueue(
                MockResponse().setResponseCode(200).setBody(jsonResponse),
            )

            val actualResponse = pullRequestDataSource.getPullRequestsForUser("exampleUser")

            val expected = Result.success(
                PullRequestsResponse(
                    incompleteResults = false,
                    totalCount = 1,
                    items = listOf(
                        PullRequest(
                            id = 1,
                            draft = false,
                            number = 1,
                            repositoryUrl = "https://api.github.com/repos/exampleOwner/exampleRepo",
                            title = "example title",
                            LocalDateTime.parse("2023-03-07T09:21:45"),
                        ),
                    ),
                ),
            )

            assertEquals(expected, actualResponse) { "Data should be valid" }
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

                val actualResponse = pullRequestDataSource.getPullRequestsForUser("exampleUser")

                val expected = Result.failure<RequestedReviewersResponse>(
                    WebException.UnknownError(
                        401,
                        "Bad credentials",
                    ),
                )

                assertEquals(expected, actualResponse) { "Data should be valid" }
            }
    }

    @Nested
    inner class GetReviewersRequestTest {

        @Test
        fun `Given request WHEN connectivity problem occurred THEN return failure with Network error`() =
            runTest {
                mockWebServer.enqueue(
                    MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY),
                )

                val actualResponse = pullRequestDataSource.getReviewers(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                )
                Assertions.assertInstanceOf(
                    WebException.NetworkError::class.java,
                    actualResponse.exceptionOrNull(),
                ) { "Exception thrown should be NetworkError type" }
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
                        .setBody(jsonResponse),
                )

                val actualResponse = pullRequestDataSource.getReviewers(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                )

                val requestedReviewer =
                    RequestedReviewer(1, "exampleLogin", "https://example/avatar")
                val expected = Result.success(RequestedReviewersResponse(listOf(requestedReviewer)))

                assertEquals(expected, actualResponse) { "Data should be valid" }
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
                    MockResponse()
                        .setResponseCode(401)
                        .setBody(jsonResponse),
                )

                val actualResponse = pullRequestDataSource.getReviewers(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                )

                val expected = Result.failure<RequestedReviewersResponse>(
                    WebException.UnknownError(
                        401,
                        "Bad credentials",
                    ),
                )

                assertEquals(expected, actualResponse) { "Data should be valid" }
            }
    }

    @Nested
    inner class GetReviewsRequestTest {

        @Test
        fun `Given request WHEN connectivity problem occurred THEN return failure with Network error`() =
            runTest {
                mockWebServer.enqueue(
                    MockResponse()
                        .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY),
                )

                val actualResponse = pullRequestDataSource.getReviews(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                )
                Assertions.assertInstanceOf(
                    WebException.NetworkError::class.java,
                    actualResponse.exceptionOrNull(),
                ) { "Exception thrown should be NetworkError type" }
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
                            "id": 10000000,
                            "node_id": "exampleNodeId",
                            "avatar_url": "https://avatars.com/u/10000000",
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
                        .setBody(jsonResponse),
                )

                val actualResponse = pullRequestDataSource.getReviews(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                )

                val expected = Result.success(
                    listOf(
                        Review(
                            "1",
                            User(10000000, "exampleUser"),
                            ReviewState.COMMENTED,
                            LocalDateTime.parse("2023-03-02T10:21:36"),
                        ),
                    ),
                )

                assertEquals(expected, actualResponse) { "Data should be valid" }
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
                    MockResponse()
                        .setResponseCode(401)
                        .setBody(jsonResponse),
                )

                val actualResponse = pullRequestDataSource.getReviews(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                )

                val expected = Result.failure<RequestedReviewersResponse>(
                    WebException.UnknownError(
                        401,
                        "Bad credentials",
                    ),
                )

                assertEquals(expected, actualResponse)
            }
    }
}
