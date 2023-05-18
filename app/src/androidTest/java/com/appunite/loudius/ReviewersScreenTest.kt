/*
 * Copyright 2023 owner S.A.
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
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ReviewersScreenTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 0)
    var mockWebServer: MockWebServerRule = MockWebServerRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Before
    fun setUp() {
        composeTestRule.registerIdlingResource(countingResource.toIdlingResource())
        hiltRule.inject()
    }

    @Test
    fun whenResponseIsCorrectThenReviewersAreVisible() {
        with(composeTestRule.activity.intent) {
            putExtra("owner", "owner")
            putExtra("repo", "repo")
            putExtra("submission_date", "2022-01-29T08:00:00")
            putExtra("pull_request_number", "1")
        }

        mockWebServer.register {
            expectThat(it).url.path.isEqualTo("/user")
            jsonResponse("""{"id": 1, "login": "user"}""")
        }
        mockWebServer.register {
            expectThat(it).url.and {
                get("host") { host }.isEqualTo("api.github.com")
                path.isEqualTo("/repos/owner/repo/pulls/1/requested_reviewers")
            }
            jsonResponse(
                """
                        {
                            "users": [
                                {
                                    "login": "userLogin",
                                    "id": 1,
                                    "node_id": "1",
                                    "avatar_url": "https://avatars.githubusercontent.com/u/18102775?v=4",
                                    "gravatar_id": "",
                                    "url": "https://api.github.com/users/user",
                                    "html_url": "https://github.com/user",
                                    "followers_url": "https://api.github.com/users/user/followers",
                                    "following_url": "https://api.github.com/users/user/following{/other_user}",
                                    "gists_url": "https://api.github.com/users/user/gists{/gist_id}",
                                    "starred_url": "https://api.github.com/users/user/starred{/owner}{/repo}",
                                    "subscriptions_url": "https://api.github.com/users/user/subscriptions",
                                    "organizations_url": "https://api.github.com/users/user/orgs",
                                    "repos_url": "https://api.github.com/users/user/repos",
                                    "events_url": "https://api.github.com/users/user/events{/privacy}",
                                    "received_events_url": "https://api.github.com/users/user/received_events",
                                    "type": "User",
                                    "site_admin": false
                                }
                            ],
                            "teams": []
                        }
                  """,
            )
        }
        mockWebServer.register {
            expectThat(it).url.and {
                get("host") { host }.isEqualTo("api.github.com")
                path.isEqualTo("/repos/owner/repo/pulls/1/reviews")
            }
            jsonResponse("[]")
        }

        composeTestRule.setContent {
            LoudiusTheme {
                ReviewersScreen { }
            }
        }

        composeTestRule.onRoot()
        composeTestRule.onNodeWithText("userLogin").assertIsDisplayed()
    }
}
