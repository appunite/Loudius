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
import androidx.compose.ui.test.performClick
import com.appunite.loudius.util.IntegrationTestRule
import com.appunite.loudius.util.waitUntilLoadingDoesNotExist
import org.junit.Before
import org.junit.Rule
import org.junit.Test

abstract class UniversalWalkThroughAppTest {

    @get:Rule(order = 0)
    val integrationTestRule by lazy { IntegrationTestRule(this, MainActivity::class.java) }

    @Before
    fun setUp() {
        integrationTestRule.setUp()
    }

    @Test
    fun whenLoginScreenIsVisible_LoginButtonOpensGithubAuth(): Unit = with(integrationTestRule) {
        composeTestRule.onNodeWithText("Log in").performClick()

        performGitHubLogin()

        composeTestRule.waitUntilLoadingDoesNotExist()

        composeTestRule.onNodeWithText("First Pull-Request title").performClick()

        composeTestRule.waitUntilLoadingDoesNotExist()

        composeTestRule.onNodeWithText("Notify").performClick()

        composeTestRule.waitUntilLoadingDoesNotExist()

        composeTestRule
            .onNodeWithText("Awesome! Your collaborator have been pinged for some serious code review action! \uD83C\uDF89")
            .assertIsDisplayed()
    }

    abstract fun performGitHubLogin()

}
