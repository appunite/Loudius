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

package com.appunite.loudius

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.appunite.loudius.common.Screen
import com.appunite.loudius.ui.components.countingResource
import com.appunite.loudius.ui.reviewers.ReviewersScreen
import com.appunite.loudius.ui.theme.LoudiusTheme
import com.appunite.loudius.util.IdlingResourceExtensions.toIdlingResource
import com.appunite.loudius.util.MockWebServerRule
import com.appunite.loudius.util.jsonResponse
import com.appunite.loudius.util.path
import com.appunite.loudius.util.url
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ReviewersScreenTest {

    companion object {
        private const val OWNER = "owner"
        private const val REPO = "repo"
        private const val DATE = "2022-01-29T08:00:00"
        private const val PR_NUMBER = "1"
    }

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 0)
    var mockWebServer: MockWebServerRule = MockWebServerRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    //@Inject
    //lateinit var repository: PullRequestRepository

    private val systemNow = LocalDateTime.parse("2022-01-29T15:00:00")
    private val systemClockFixed =
        Clock.fixed(systemNow.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"))
//
//    private val repository: PullRequestRepository = mockk(relaxed = true)

//    private lateinit var viewModel: ReviewersViewModel
//
//    private fun createViewModel() = ReviewersViewModel(repository, savedStateHandle)

    @Before
    fun setUp() {
        composeTestRule.registerIdlingResource(countingResource.toIdlingResource())
        hiltRule.inject()
        //MockKAnnotations.init(this)
        mockkStatic(Clock::class)
        every { Clock.systemDefaultZone() } returns systemClockFixed
    }

    @Test
    fun whenResponseIsCorrectThenReviewersAreVisible() {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "owner" to OWNER,
                "repo" to REPO,
                "submission_date" to DATE,
                "pull_request_number" to PR_NUMBER
            )
        )

        every {
            Screen.Reviewers.getInitialValues(savedStateHandle)
        } returns Screen.Reviewers.ReviewersInitialValues(
            owner = checkNotNull(savedStateHandle["owner"]),
            repo = checkNotNull(savedStateHandle["repo"]),
            pullRequestNumber = checkNotNull(savedStateHandle["pull_request_number"]),
            submissionTime = checkNotNull(LocalDateTime.parse(savedStateHandle["submission_date"]))
        )

        mockWebServer.register {
            expectThat(it).url.path.isEqualTo("/user")

            jsonResponse("""{"id": 1, "login": "user"}""")
        }
        mockWebServer.register {
            expectThat(it).url.and {
                get("host") { host }.isEqualTo("api.github.com")
                path.isEqualTo("/repos/appunite/Loudius/pulls/41/requested_reviewers")
            }

            jsonResponse(
                """
            {
                "users": [
                    {
                        "login": "kezc",
                        "id": 1,
                        "node_id": "1",
                        "avatar_url": "https://avatars.githubusercontent.com/u/18102775?v=4",
                        "gravatar_id": "",
                        "url": "https://api.github.com/users/kezc",
                        "html_url": "https://github.com/kezc",
                        "followers_url": "https://api.github.com/users/kezc/followers",
                        "following_url": "https://api.github.com/users/kezc/following{/other_user}",
                        "gists_url": "https://api.github.com/users/kezc/gists{/gist_id}",
                        "starred_url": "https://api.github.com/users/kezc/starred{/owner}{/repo}",
                        "subscriptions_url": "https://api.github.com/users/kezc/subscriptions",
                        "organizations_url": "https://api.github.com/users/kezc/orgs",
                        "repos_url": "https://api.github.com/users/kezc/repos",
                        "events_url": "https://api.github.com/users/kezc/events{/privacy}",
                        "received_events_url": "https://api.github.com/users/kezc/received_events",
                        "type": "User",
                        "site_admin": false
                    },
                    {
                        "login": "Krzysiudan",
                        "id": 1,
                        "node_id": "1",
                        "avatar_url": "https://avatars.githubusercontent.com/u/33498031?v=4",
                        "gravatar_id": "",
                        "url": "https://api.github.com/users/Krzysiudan",
                        "html_url": "https://github.com/Krzysiudan",
                        "followers_url": "https://api.github.com/users/Krzysiudan/followers",
                        "following_url": "https://api.github.com/users/Krzysiudan/following{/other_user}",
                        "gists_url": "https://api.github.com/users/Krzysiudan/gists{/gist_id}",
                        "starred_url": "https://api.github.com/users/Krzysiudan/starred{/owner}{/repo}",
                        "subscriptions_url": "https://api.github.com/users/Krzysiudan/subscriptions",
                        "organizations_url": "https://api.github.com/users/Krzysiudan/orgs",
                        "repos_url": "https://api.github.com/users/Krzysiudan/repos",
                        "events_url": "https://api.github.com/users/Krzysiudan/events{/privacy}",
                        "received_events_url": "https://api.github.com/users/Krzysiudan/received_events",
                        "type": "User",
                        "site_admin": false
                    }
                ],
                "teams": []
            }
        """,
            )
        }

        composeTestRule.setContent {
            LoudiusTheme {
                ReviewersScreen { }
            }
        }

        composeTestRule.onRoot()
        composeTestRule.onNodeWithText("kezc").assertIsDisplayed()
    }

    @Test
    fun whenClickOnNotifyThenNotifyReviewer() {

    }
}

private val reviewersJsonResponse = """
            {
                "users": [
                    {
                        "login": "kezc",
                        "id": 1,
                        "node_id": "1",
                        "avatar_url": "https://avatars.githubusercontent.com/u/18102775?v=4",
                        "gravatar_id": "",
                        "url": "https://api.github.com/users/kezc",
                        "html_url": "https://github.com/kezc",
                        "followers_url": "https://api.github.com/users/kezc/followers",
                        "following_url": "https://api.github.com/users/kezc/following{/other_user}",
                        "gists_url": "https://api.github.com/users/kezc/gists{/gist_id}",
                        "starred_url": "https://api.github.com/users/kezc/starred{/owner}{/repo}",
                        "subscriptions_url": "https://api.github.com/users/kezc/subscriptions",
                        "organizations_url": "https://api.github.com/users/kezc/orgs",
                        "repos_url": "https://api.github.com/users/kezc/repos",
                        "events_url": "https://api.github.com/users/kezc/events{/privacy}",
                        "received_events_url": "https://api.github.com/users/kezc/received_events",
                        "type": "User",
                        "site_admin": false
                    },
                    {
                        "login": "Krzysiudan",
                        "id": 1,
                        "node_id": "1",
                        "avatar_url": "https://avatars.githubusercontent.com/u/33498031?v=4",
                        "gravatar_id": "",
                        "url": "https://api.github.com/users/Krzysiudan",
                        "html_url": "https://github.com/Krzysiudan",
                        "followers_url": "https://api.github.com/users/Krzysiudan/followers",
                        "following_url": "https://api.github.com/users/Krzysiudan/following{/other_user}",
                        "gists_url": "https://api.github.com/users/Krzysiudan/gists{/gist_id}",
                        "starred_url": "https://api.github.com/users/Krzysiudan/starred{/owner}{/repo}",
                        "subscriptions_url": "https://api.github.com/users/Krzysiudan/subscriptions",
                        "organizations_url": "https://api.github.com/users/Krzysiudan/orgs",
                        "repos_url": "https://api.github.com/users/Krzysiudan/repos",
                        "events_url": "https://api.github.com/users/Krzysiudan/events{/privacy}",
                        "received_events_url": "https://api.github.com/users/Krzysiudan/received_events",
                        "type": "User",
                        "site_admin": false
                    }
                ],
                "teams": []
            }
        """.trimIndent()

private val prsJsonResponse = """
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
                            "title":"First Pull-Request title",
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
