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

package com.appunite.loudius.util

import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Condition
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.rules.TestWatcher
import org.junit.runner.Description

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

/**
 * Type some text
 *
 * **Example:**
 *
 * ```kotlin
 * device.type("jacek.marchwicki@gmail.com")
 * ```
 */
fun UiDevice.type(text: String) {
    val keyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
    waitForIdle()
    groupKeys(keyMap.getEvents(text.toCharArray()).toList())
        .forEach {
            pressKeyCodes(
                it.keyCodes.toIntArray(),
                it.metaState,
            )
        }
    waitForIdle()
}

/**
 * Wait for a view to appear on the screen, and returns it for actions
 *
 * **Example:**
 *
 * To wait for "Some button" and click it.
 * ```kotlin
 * device.waitAndFind(By.text("Some button"))
 *   .click()
 * ```
 */
fun UiDevice.waitAndFind(
    selector: BySelector,
): UiObject2 {
    ensure(Until.hasObject(selector))

    return findObject(selector) ?: throw AssertionError("Could not find object: $selector")
}

/**
 * Ensures given condition is satisfied before continuing executing.
 *
 * **Example:**
 *
 * To wait for "Some text" and ensure it is displayed on the screen.
 *
 * ```kotlin
 * device.ensure(Until.hasObject(By.text("Some text"))
 * ```
 */
fun UiDevice.ensure(
    condition: Condition<in UiDevice, Boolean>,
    timeout: Long = 30_000L,
) {
    val result = wait(condition, timeout) ?: throw AssertionError("Error in condition")
    if (!result) {
        throw AssertionError("Could not satisfy: $condition")
    }
}

/**
 * Condition that returns true if any one of selectors is displayed
 *
 * **Example:**
 *
 * To wait for "Some text" or "Other text" is displayed on the screen:
 *
 * ```kotlin
 * device.ensure(hasAnyOfObjects(
 *   By.text("Some text"),
 *   By.text("Other text")
 * )
 * ```
 */
fun hasAnyOfObjects(vararg selectors: BySelector): Condition<UiDevice, Boolean> {
    return object : Condition<UiDevice, Boolean> {
        override fun apply(device: UiDevice): Boolean =
            selectors.any { selector -> device.hasObject(selector) }

        override fun toString(): String =
            "hasAnyOfObjects[${selectors.joinToString(separator = ",")}]"
    }
}