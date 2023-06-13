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

import androidx.test.espresso.Espresso
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.atomic.AtomicBoolean

/**
 * TestRule used to take screenshot on failure.
 */
open class ScreenshotTestRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                var error: Throwable? = null
                val errorHandled = AtomicBoolean(false)

                Espresso.setFailureHandler { throwable, matcher ->
                    Screenshot.capture().process()
                    errorHandled.set(true)
                    val targetContext = getInstrumentation().targetContext
                    DefaultFailureHandler(targetContext).handle(throwable, matcher)
                }

                errorHandled.set(false)
                try {
                    base.evaluate()
                    return
                } catch (t: Throwable) {
                    if (!errorHandled.get()) {
                        captureScreenshot(description)
                    }
                    error = t
                }

                if (error != null) throw error
            }
        }
    }

    private fun captureScreenshot(description: Description) {
        val screenshot = Screenshot.capture()
        screenshot.name = "${description.testClass.simpleName}_${description.methodName}-failure"
        screenshot.process()
    }
}
