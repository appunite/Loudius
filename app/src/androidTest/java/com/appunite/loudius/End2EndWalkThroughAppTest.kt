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

import android.view.KeyEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.appunite.loudius.util.AutomatorTestRule
import com.appunite.loudius.util.description
import com.appunite.loudius.util.ensure
import com.appunite.loudius.util.generateOtp
import com.appunite.loudius.util.githubUserName
import com.appunite.loudius.util.githubUserPassword
import com.appunite.loudius.util.hasAnyOfObjects
import com.appunite.loudius.util.type
import com.appunite.loudius.util.waitAndFind
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class End2EndWalkThroughAppTest : UniversalWalkThroughAppTest() {

    @get:Rule
    val automatorTestRule: AutomatorTestRule = AutomatorTestRule()

    @Before
    fun prepareGoogleChrome() {
        // Clear Google Chrome user data to ensure that Google Chrome behaves always the same
        automatorTestRule.device.executeShellCommand("pm clear com.android.chrome")
    }

    override fun performGitHubLogin(): Unit = with(integrationTestRule) {
        // Here we need to use `composeTestRule.waitUntil` instead of `device.waitAndFind`
        // because compose actions need to finished to execute startActivity().
        composeTestRule.waitUntil(30_000L) {
            automatorTestRule.device.findObject(By.pkg("com.android.chrome")) != null
        }

        description("Wait for onboarding or the webpage") {
            automatorTestRule.device.ensure(
                hasAnyOfObjects(
                    By.text("Accept & continue"),
                    By.text("Username or email address"),
                ),
            )
        }

        description("Skip Google Chrome onboarding process") {
            val hasOnboarding =
                automatorTestRule.device.findObject(By.text("Accept & continue")) != null
            if (hasOnboarding) {
                automatorTestRule.device.waitAndFind(By.text("Accept & continue"))
                    .clickAndWait(Until.newWindow(), 3_000L)

                automatorTestRule.device.waitAndFind(By.text("No thanks"))
                    .clickAndWait(Until.newWindow(), 3_000L)

                automatorTestRule.device.waitAndFind(By.text("No thanks"))
                    .clickAndWait(Until.newWindow(), 3_000L)
            }
        }

        description("Fill user name") {
            automatorTestRule.device.waitAndFind(By.text("Username or email address"))
                .click()
            Thread.sleep(5_000L)
            automatorTestRule.device.type(githubUserName)
        }

        description("Fill password") {
            automatorTestRule.device.pressKeyCode(KeyEvent.KEYCODE_TAB)
            automatorTestRule.device.type(githubUserPassword)
        }

        description("Click log-in") {
            automatorTestRule.device.pressEnter()
        }

        description("Fill authentication code") {
            automatorTestRule.device.waitAndFind(By.text("Verify"))
            automatorTestRule.device.type(generateOtp())
        }

        description("Wait for return to the app") {
            automatorTestRule.device.waitAndFind(
                By.pkg(InstrumentationRegistry.getInstrumentation().targetContext.packageName),
            )
        }
    }
}

