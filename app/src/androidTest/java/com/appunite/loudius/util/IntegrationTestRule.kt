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


import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.appunite.loudius.components.components.countingResource
import com.appunite.loudius.util.IdlingResourceExtensions.toIdlingResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class IntegrationTestRule(testActivity: Class<out ComponentActivity> = ComponentActivity::class.java) : TestRule {

    val mockWebServer = MockWebServerRule()
    val composeTestRule = createAndroidComposeRule(testActivity).apply {
        registerIdlingResource(countingResource.toIdlingResource())
    }
    private val screenshotTestRule = ScreenshotTestRule()

    override fun apply(base: Statement, description: Description): Statement {
        return RuleChain.outerRule(mockWebServer)
            .around(composeTestRule)
            .around(screenshotTestRule)
            .apply(base, description)
    }
}
