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

import com.appunite.loudius.network.httpClientTestDouble
import com.appunite.loudius.network.model.RequestedReviewer
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState
import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.services.PullRequestsServiceImpl
import com.appunite.loudius.util.Defaults
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isSuccess
import strikt.assertions.single
import java.net.ConnectException

@ExperimentalCoroutinesApi
class PullRequestsDataSourceImplTest {

    private lateinit var client: HttpClient
    private lateinit var pullRequestsService: PullRequestsServiceImpl
    private lateinit var pullRequestDataSource: PullRequestsDataSourceImpl
    private val mockWebServer = MockWebServer()

    @BeforeEach
    fun setUp() {
        mockWebServer.start()
        client = httpClientTestDouble(mockWebServer)
        pullRequestsService = PullRequestsServiceImpl(client)
        pullRequestDataSource = PullRequestsDataSourceImpl(pullRequestsService)
    }

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
                    MockResponse()
                        .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY)
                        .addHeader("Content-type", "application/json")
                )

                val response = pullRequestDataSource.getPullRequestsForUser(
                    "exampleUser"
                )

                expectThat(response)
                    .isFailure()
                    .isA<ContentConvertException>()
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
                MockResponse()
                    .setResponseCode(200)
                    .setBody(jsonResponse)
                    .addHeader("Content-type", "application/json")
            )

            val response = pullRequestDataSource.getPullRequestsForUser("exampleUser")

            expectThat(response).isSuccess()
                .isEqualTo(Defaults.pullRequestsResponse())
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
                        .setBody(jsonResponse)
                        .addHeader("Content-type", "application/json")
                )

                val response = pullRequestDataSource.getPullRequestsForUser("exampleUser")

                expectThat(response)
                    .isFailure()
                    .isA<ClientRequestException>()
            }
    }

    @Nested
    inner class GetReviewersRequestTest {

        @Test
        fun `Given request WHEN connectivity problem occurred THEN return failure with Network error`() =
            runTest {
                mockWebServer.enqueue(
                    MockResponse()
                        .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY)
                        .addHeader("Content-type", "application/json")
                )

                val response = pullRequestDataSource.getReviewers(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber"
                )

                expectThat(response)
                    .isFailure()
                    .isA<ContentConvertException>()
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
                        .addHeader("Content-type", "application/json")
                )

                val response = pullRequestDataSource.getReviewers(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber"
                )

                expectThat(response)
                    .isSuccess()
                    .isEqualTo(
                        RequestedReviewersResponse(
                            listOf(
                                RequestedReviewer(1, "exampleLogin")
                            )
                        )
                    )
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
                        .setBody(jsonResponse)
                        .addHeader("Content-type", "application/json")
                )

                val response = pullRequestDataSource.getReviewers(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber"
                )

                expectThat(response)
                    .isFailure()
                    .isA<ClientRequestException>()
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
                        .addHeader("Content-type", "application/json")
                )

                val resposne = pullRequestDataSource.getReviews(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber"
                )

                expectThat(resposne)
                    .isFailure()
                    .isA<ContentConvertException>()
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
                        .setBody(jsonResponse)
                        .addHeader("Content-type", "application/json")
                )

                val response = pullRequestDataSource.getReviews(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber"
                )

                expectThat(response)
                    .isSuccess()
                    .single()
                    .isEqualTo(
                        Review(
                            "1",
                            User(10000000, "exampleUser"),
                            ReviewState.COMMENTED,
                            Instant.parse("2023-03-02T10:21:36Z")
                        ),
                    )
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
                        .setBody(jsonResponse)
                        .addHeader("Content-type", "application/json")
                )

                val response = pullRequestDataSource.getReviews(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber"
                )

                expectThat(response)
                    .isFailure()
                    .isA<ClientRequestException>()
            }
    }

    @Nested
    inner class NotifyRequestTest {

        @Test
        fun `GIVEN connectivity problem WHEN request THEN return failure with Network error`() =
            runTest {
                mockWebServer.enqueue(
                    MockResponse()
                        .setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)
                        .addHeader("Content-type", "application/json")
                )

                val response = pullRequestDataSource.notify(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                    "@ExampleUser"
                )

                expectThat(response)
                    .isFailure()
                    .isA<ConnectException>()
            }

        @Test
        fun `GIVEN correct params WHEN successful response THEN return success result`() = runTest {
            // language=JSON
            val jsonResponse = """
                {
                  "id": 1,
                  "node_id": "MDEyOklzc3VlQ29tbWVudDE=",
                  "url": "https://api.github.com/repos/octocat/Hello-World/issues/comments/1",
                  "html_url": "https://github.com/octocat/Hello-World/issues/1347#issuecomment-1",
                  "body": "Me too",
                  "user": {
                    "login": "octocat",
                    "id": 1,
                    "node_id": "MDQ6VXNlcjE=",
                    "avatar_url": "https://github.com/images/error/octocat_happy.gif",
                    "gravatar_id": "",
                    "url": "https://api.github.com/users/octocat",
                    "html_url": "https://github.com/octocat",
                    "followers_url": "https://api.github.com/users/octocat/followers",
                    "following_url": "https://api.github.com/users/octocat/following{/other_user}",
                    "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
                    "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
                    "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
                    "organizations_url": "https://api.github.com/users/octocat/orgs",
                    "repos_url": "https://api.github.com/users/octocat/repos",
                    "events_url": "https://api.github.com/users/octocat/events{/privacy}",
                    "received_events_url": "https://api.github.com/users/octocat/received_events",
                    "type": "User",
                    "site_admin": false
                  },
                  "created_at": "2011-04-14T16:00:49Z",
                  "updated_at": "2011-04-14T16:00:49Z",
                  "issue_url": "https://api.github.com/repos/octocat/Hello-World/issues/1347",
                  "author_association": "COLLABORATOR"
                }
            """.trimIndent()
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(jsonResponse)
                    .addHeader("Content-type", "application/json")
            )

            val response = pullRequestDataSource.notify(
                "exampleOwner",
                "exampleRepo",
                "exampleNumber",
                "@ExampleUser"
            )

            expectThat(response).isSuccess()
        }

        @Test
        fun `GIVEN auth error WHEN processing request THEN return failure with Unknown error`() =
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
                        .addHeader("Content-type", "application/json")
                )

                val response = pullRequestDataSource.notify(
                    "exampleOwner",
                    "exampleRepo",
                    "exampleNumber",
                    "@ExampleUser"
                )

                expectThat(response)
                    .isFailure()
                    .isA<ClientRequestException>()
            }
    }
}
