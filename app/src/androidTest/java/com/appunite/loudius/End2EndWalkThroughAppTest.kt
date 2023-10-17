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

import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Condition
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.appunite.loudius.util.githubUserName
import com.appunite.loudius.util.githubUserPassword
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class End2EndWalkThroughAppTest : UniversalWalkThroughAppTest() {

    @get:Rule
    val automatorTestRule: AutomatorTestRule = AutomatorTestRule()

    @Before
    fun prepareGoogleChrome() {
        automatorTestRule.device.executeShellCommand("pm clear com.android.chrome")
    }

    override fun performGitHubLogin(): Unit = with(integrationTestRule) {
        // Here we need to use `composeTestRule.waitUntil` instead of `device.waitAndFind`
        // because compose actions need to finished to execute startActivity().
        composeTestRule.waitUntil(30_000L) {
            automatorTestRule.device.findObject(By.pkg("com.android.chrome")) != null
        }

        // Wait for onboarding or the webpage
        automatorTestRule.device.wait(
            hasAnyOfObjects(
                By.text("Accept & continue"),
                By.text("Username or email address"),
            ),
            30_000L,
        )

        // Google Chrome onboarding process
        val hasOnboarding = automatorTestRule.device.findObject(By.text("Accept & continue")) != null
        if (hasOnboarding) {
            automatorTestRule.device.waitAndFind(By.text("Accept & continue"))
                .clickAndWait(Until.newWindow(), 3_000L)

            automatorTestRule.device.waitAndFind(By.text("No thanks"))
                .clickAndWait(Until.newWindow(), 3_000L)

            automatorTestRule.device.waitAndFind(By.text("No thanks"))
                .clickAndWait(Until.newWindow(), 3_000L)
        }

        // Fill user name
        automatorTestRule.device.waitAndFind(By.text("Username or email address"))
            .click()
        Thread.sleep(5_000L)
        automatorTestRule.device.type(githubUserName)

        // Fill password
        automatorTestRule.device.pressKeyCode(KeyEvent.KEYCODE_TAB)
        automatorTestRule.device.type(githubUserPassword)

        // Click log-in
        automatorTestRule.device.pressEnter()

        // Wait for return to the app
        automatorTestRule.device.waitAndFind(
            By.pkg(InstrumentationRegistry.getInstrumentation().targetContext.packageName),
        )
    }
}

class AutomatorTestRule : TestWatcher() {
    private var internalDevice: UiDevice? = null
    val device: UiDevice get() = internalDevice!!

    override fun starting(description: Description?) {
        internalDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    override fun finished(description: Description?) {
        internalDevice = null
    }
}

private fun UiDevice.type(text: String) {
    val keyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
    waitForIdle()
    keyMap.getEvents(text.toCharArray())
        .filter { it.action == KeyEvent.ACTION_UP }
        .forEach {
            pressKeyCode(
                it.keyCode,
                it.metaState,
            )
        }
    waitForIdle()
}

private fun UiDevice.waitAndFind(
    selector: BySelector,
): UiObject2 {
    wait(Until.hasObject(selector), 30_000L)

    return findObject(selector)
        ?: throw AssertionError("Could not find object")
}

private fun hasAnyOfObjects(vararg selectors: BySelector): Condition<UiDevice, Boolean> {
    return Condition<UiDevice, Boolean> { device -> selectors.any { selector -> device.hasObject(selector) } }
}
