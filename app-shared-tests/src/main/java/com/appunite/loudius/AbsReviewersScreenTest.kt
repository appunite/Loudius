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
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.appunite.loudius.components.theme.LoudiusTheme
import com.appunite.loudius.ui.reviewers.ReviewersScreen
import com.appunite.loudius.util.IntegrationTestRule
import com.appunite.loudius.util.Register
import com.appunite.loudius.util.waitUntilLoadingDoesNotExist
import org.junit.Before
import org.junit.Rule
import org.junit.Test


abstract class AbsReviewersScreenTest {

    @get:Rule
    val integrationTestRule = IntegrationTestRule()

    @Before
    fun setUp() {
        integrationTestRule.initTests()
    }

    @Test
    fun whenResponseIsCorrectThenReviewersAreVisible() {
        with(integrationTestRule) {
            composeTestRule.setContent {
                LoudiusTheme {
                    ReviewersScreen { }
                }
            }

            composeTestRule.waitUntilLoadingDoesNotExist()

            composeTestRule.onNodeWithText("userLogin").assertIsDisplayed()
        }
    }

    @Test
    fun whenClickOnNotifyAndCommentThenNotifyReviewer() {
        with(integrationTestRule) {
            Register.comment(mockWebServer)

            composeTestRule.setContent {
                LoudiusTheme {
                    ReviewersScreen { }
                }
            }

            composeTestRule.waitUntilLoadingDoesNotExist()

            composeTestRule.onNodeWithText("Notify").performClick()

            composeTestRule.waitUntilLoadingDoesNotExist()

            composeTestRule
                .onNodeWithText("Awesome! Your collaborator have been pinged for some serious code review action! \uD83C\uDF89")
                .assertIsDisplayed()
        }
    }

    @Test
    fun whenClickOnNotifyAndDoNotCommentThenShowError() {
        with(integrationTestRule) {
            composeTestRule.setContent {
                LoudiusTheme {
                    ReviewersScreen { }
                }
            }

            composeTestRule.waitUntilLoadingDoesNotExist()

            composeTestRule.onNodeWithText("Notify").performClick()

            composeTestRule.waitUntilLoadingDoesNotExist()

            composeTestRule
                .onNodeWithText("Uh-oh, it seems that Loudius has taken a vacation. Don't worry, we're sending a postcard to bring it back ASAP!")
                .assertIsDisplayed()
        }
    }

    private fun IntegrationTestRule.initTests() {
        composeTestRule.activity.intent.apply {
            putExtra("owner", "exampleOwner")
            putExtra("repo", "exampleRepo")
            putExtra("submission_date", "2022-01-29T08:00:00")
            putExtra("pull_request_number", "1")
        }
        Register.user(mockWebServer)
        Register.requestedReviewers(mockWebServer)
        Register.reviews(mockWebServer)
    }

}
