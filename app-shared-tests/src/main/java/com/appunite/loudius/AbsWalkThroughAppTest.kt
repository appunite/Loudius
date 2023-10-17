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

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsRule
import com.appunite.loudius.util.IntegrationTestRule
import com.appunite.loudius.util.Register
import com.appunite.loudius.util.waitUntilLoadingDoesNotExist
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin

abstract class AbsWalkThroughAppTest {

    @get:Rule(order = 0)
    val integrationTestRule by lazy { IntegrationTestRule(MainActivity::class.java) }

    @get:Rule(order = 1)
    val intents = IntentsRule()

    @Test
    fun whenLoginScreenIsVisible_LoginButtonOpensGithubAuth(): Unit = with(integrationTestRule) {
        Register.run {
            user(mockWebServer)
            accessToken(mockWebServer)
            issues(mockWebServer)
            requestedReviewers(mockWebServer)
            reviews(mockWebServer)
            comment(mockWebServer)
        }

        intending(IntentMatchers.hasData("https://github.com/login/oauth/authorize?client_id=91131449e417c7e29912&scope=repo"))
            .respondWithFunction {
                Instrumentation.ActivityResult(Activity.RESULT_OK, null)
            }

        composeTestRule.onNodeWithText("Log in").performClick()

        // simulate opening a deeplink
        ActivityScenario.launch<MainActivity>(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("loudius://callback?code=example_code"),
            ).apply {
                setPackage(composeTestRule.activity.packageName)
            },
        )

        composeTestRule.waitUntilLoadingDoesNotExist()

        composeTestRule.onNodeWithText("First Pull-Request title").performClick()

        composeTestRule.waitUntilLoadingDoesNotExist()

        composeTestRule.onNodeWithText("Notify").performClick()

        composeTestRule.waitUntilLoadingDoesNotExist()

        composeTestRule
            .onNodeWithText("Awesome! Your collaborator have been pinged for some serious code review action! \uD83C\uDF89")
            .assertIsDisplayed()
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}
