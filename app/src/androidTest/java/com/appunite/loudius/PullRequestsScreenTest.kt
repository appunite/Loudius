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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.appunite.loudius.ui.pullrequests.PullRequestsScreen
import com.appunite.loudius.ui.theme.LoudiusTheme
import com.appunite.loudius.util.Register
import com.appunite.loudius.util.TestRules
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PullRequestsScreenTest {

    @get:Rule
    val testRules = TestRules(this)

    @Before
    fun setUp() {
        testRules.setUp()
    }

    @Test
    fun whenResponseIsCorrectThenPullRequestItemIsVisible() {
        with(testRules) {
            with(Register) {
                user(mockWebServer)
                issues(mockWebServer)
            }

            composeTestRule.setContent {
                LoudiusTheme {
                    PullRequestsScreen { _, _, _, _ -> }
                }
            }

            composeTestRule.onNodeWithText("First Pull-Request title").assertIsDisplayed()
        }
    }
}
