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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.appunite.loudius.ui.reviewers.ReviewersScreen
import com.appunite.loudius.ui.theme.LoudiusTheme
import com.appunite.loudius.util.IntegrationTestRule
import com.appunite.loudius.util.Register
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ReviewersScreenTest {

    @get:Rule
    val integrationTestRule = IntegrationTestRule(this)

    @Before
    fun setUp() {
        integrationTestRule.setUp()
    }

    @Test
    fun whenResponseIsCorrectThenReviewersAreVisible() {
        with(integrationTestRule) {
            with(composeTestRule.activity.intent) {
                putExtra("owner", "owner")
                putExtra("repo", "repo")
                putExtra("submission_date", "2022-01-29T08:00:00")
                putExtra("pull_request_number", "1")
            }

            Register.user(mockWebServer)
            Register.requestedReviewers(mockWebServer)
            Register.reviews(mockWebServer)

            composeTestRule.setContent {
                LoudiusTheme {
                    ReviewersScreen { }
                }
            }

            composeTestRule.onNodeWithText("userLogin").assertIsDisplayed()
        }
    }
}
