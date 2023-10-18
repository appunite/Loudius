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
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasPackageName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.rule.IntentsRule
import com.appunite.loudius.components.theme.LoudiusTheme
import com.appunite.loudius.ui.login.GithubHelper
import com.appunite.loudius.ui.login.LoginScreen
import com.appunite.loudius.util.ScreenshotTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

abstract class AbsLoginScreenTest {

    @get:Rule(order = 0)
    val hiltRule by lazy { HiltAndroidRule(this) }

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @get:Rule(order = 2)
    val intents = IntentsRule()

    @Rule
    @JvmField
    val screenshotTestRule = ScreenshotTestRule()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    val githubHelper: GithubHelper = mockk<GithubHelper>().apply {
        every { shouldAskForXiaomiIntent() } returns false
    }

    @Test
    fun whenLoginScreenIsVisible_LoginButtonOpensGithubAuth() {
        intending(hasAction(Intent.ACTION_VIEW))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        composeTestRule.setContent {
            LoudiusTheme {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithText("Log in").performClick()

        composeTestRule.waitForIdle()
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData("https://github.com/login/oauth/authorize?client_id=91131449e417c7e29912&scope=repo"),
            ),
        )
    }

    @Test
    fun whenClickingPermissionGrantedInXiaomiDialog_OpenGithubAuth() {
        every { githubHelper.shouldAskForXiaomiIntent() } returns true
        intending(hasAction(Intent.ACTION_VIEW))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        composeTestRule.setContent {
            LoudiusTheme {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithText("Log in").performClick()
        composeTestRule.onNodeWithText("I've already granted").performClick()

        composeTestRule.waitForIdle()
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData("https://github.com/login/oauth/authorize?client_id=91131449e417c7e29912&scope=repo")
            )
        )
    }

    @Test
    fun whenClickingGrantPermissionInXiaomiDialog_OpenPermissionEditor() {
        every { githubHelper.shouldAskForXiaomiIntent() } returns true
        intending(hasAction("miui.intent.action.APP_PERM_EDITOR"))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        composeTestRule.setContent {
            LoudiusTheme {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithText("Log in").performClick()
        composeTestRule.onNodeWithText("Grant permission").performClick()

        composeTestRule.waitForIdle()
        intended(
            allOf(
                hasAction("miui.intent.action.APP_PERM_EDITOR"),
                hasExtra("extra_pkgname", "com.github.android"),
                hasComponent(
                    allOf(
                        hasPackageName("com.miui.securitycenter"),
                        hasClassName("com.miui.permcenter.permissions.PermissionsEditorActivity")
                    )
                )
            )
        )
    }
}
