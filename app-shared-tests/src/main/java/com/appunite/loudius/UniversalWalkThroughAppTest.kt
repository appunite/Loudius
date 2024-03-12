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
import androidx.compose.ui.test.onNodeWithContentDescription
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

        composeTestRule.onNodeWithContentDescription("Back button").performClick()

        composeTestRule.waitUntilLoadingDoesNotExist()

        expectThat(analyticsRule.analytics.log).containsExactly(
            AnalyticsLog("screen_opened", mapOf("screen_name" to "log_in_screen")),
            AnalyticsLog("button_click", mapOf("item_name" to "log_in", "screen_name" to "log_in_screen")),
            AnalyticsLog("simple_action", mapOf("item_name" to "open_github_auth", "screen_name" to "log_in_screen")),
            AnalyticsLog("action_start", mapOf("item_name" to "authentication", "screen_name" to "authenticating_screen")),
            AnalyticsLog("action_start", mapOf("item_name" to "get_access_token", "screen_name" to "authenticating_screen")),
            AnalyticsLog("screen_opened", mapOf("screen_name" to "authenticating_screen")),
            AnalyticsLog("action_finished", mapOf("item_name" to "get_access_token", "action_success" to true, "screen_name" to "authenticating_screen")),
            AnalyticsLog("action_finished", mapOf("item_name" to "authentication", "action_success" to true, "screen_name" to "authenticating_screen")),
            AnalyticsLog("action_start", mapOf("item_name" to "fetch_pull_requests_data", "screen_name" to "pull_requests_screen")),
            AnalyticsLog("screen_opened", mapOf("screen_name" to "pull_requests_screen")),
            AnalyticsLog("action_finished", mapOf("item_name" to "fetch_pull_requests_data", "action_success" to true, "screen_name" to "pull_requests_screen")),
            AnalyticsLog("item_click", mapOf("item_name" to "pull_request", "screen_name" to "pull_requests_screen")),
            AnalyticsLog("action_start", mapOf("item_name" to "fetch_reviewers_data", "screen_name" to "reviewers_screen")),
            AnalyticsLog("screen_opened", mapOf("screen_name" to "reviewers_screen")),
            AnalyticsLog("action_finished", mapOf("item_name" to "fetch_reviewers_data", "action_success" to true, "screen_name" to "reviewers_screen")),
            AnalyticsLog("button_click", mapOf("item_name" to "notify", "screen_name" to "reviewers_screen")),
            AnalyticsLog("action_start", mapOf("item_name" to "notify", "screen_name" to "reviewers_screen")),
            AnalyticsLog("action_finished", mapOf("item_name" to "notify", "action_success" to true, "screen_name" to "reviewers_screen")),
            AnalyticsLog("screen_opened", mapOf("screen_name" to "pull_requests_screen"))
        )
    }

    abstract fun performGitHubLogin()
}
