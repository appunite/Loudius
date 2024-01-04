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
import com.appunite.loudius.analytics.AnalyticsLog
import com.appunite.loudius.analytics.AnalyticsRule
import com.appunite.loudius.util.IntegrationTestRule
import com.appunite.loudius.util.waitUntilLoadingDoesNotExist
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly

abstract class UniversalWalkThroughAppTest {

    @get:Rule(order = 0)
    val integrationTestRule by lazy { IntegrationTestRule(MainActivity::class.java) }

    @get:Rule
    val analyticsRule = AnalyticsRule()

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

        expectThat(analyticsRule.analytics.log).containsExactly(
            AnalyticsLog("screen_opened", mapOf("item_name" to "log_in_screen")),
            AnalyticsLog("button_click", mapOf("item_name" to "log_in")),
            AnalyticsLog("simple_action", mapOf("item_name" to "open_github_auth")),
            AnalyticsLog("action_start", mapOf("item_name" to "authentication")),
            AnalyticsLog("action_finished", mapOf("item_name" to "get_access_token", "success" to true)),
            AnalyticsLog("action_finished", mapOf("item_name" to "authentication", "success" to true)),
            AnalyticsLog("action_start", mapOf("item_name" to "fetch_pull_requests_data")),
            AnalyticsLog("screen_opened", mapOf("item_name" to "pull_requests_screen")),
            AnalyticsLog("action_finished", mapOf("item_name" to "fetch_pull_requests_data", "success" to true)),
            AnalyticsLog("item_click", mapOf("item_name" to "pull_request")),
            AnalyticsLog("action_start", mapOf("item_name" to "fetch_reviewers_data")),
            AnalyticsLog("screen_opened", mapOf("item_name" to "reviewers_screen")),
            AnalyticsLog("action_finished", mapOf("item_name" to "fetch_reviewers_data", "success" to true)),
            AnalyticsLog("button_click", mapOf("item_name" to "notify")),
            AnalyticsLog("action_start", mapOf("item_name" to "notify")),
            AnalyticsLog("action_finished", mapOf("item_name" to "notify", "success" to true))
        )
    }

    abstract fun performGitHubLogin()
}
