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
import androidx.compose.ui.test.onNodeWithText
import com.appunite.loudius.components.theme.LoudiusTheme
import com.appunite.loudius.ui.pullrequests.PullRequestsScreen
import com.appunite.loudius.util.IntegrationTestRule
import com.appunite.loudius.util.Register
import com.appunite.loudius.util.waitUntilLoadingDoesNotExist
import org.junit.Before
import org.junit.Rule
import org.junit.Test

abstract class AbsPullRequestsScreenTest {

    @get:Rule
    val integrationTestRule by lazy { IntegrationTestRule(this) }

    @Before
    fun setUp() {
        integrationTestRule.setUp()
    }

    @Test
    fun whenResponseIsCorrectThenPullRequestItemIsVisible() {
        with(integrationTestRule) {
            Register.user(mockWebServer)
            Register.issues(mockWebServer)

            composeTestRule.setContent {
                LoudiusTheme {
                    PullRequestsScreen { _, _, _, _ -> }
                }
            }

            composeTestRule.waitUntilLoadingDoesNotExist()

            composeTestRule.onNodeWithText("First Pull-Request title").assertIsDisplayed()
        }
    }
}