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

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.appunite.loudius.TestActivity
import com.appunite.loudius.ui.components.countingResource
import com.appunite.loudius.util.IdlingResourceExtensions.toIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@HiltAndroidTest
class TestRules(testClass: Any) : TestRule {

    val mockWebServer = MockWebServerRule()
    val composeTestRule = createAndroidComposeRule<TestActivity>()
    private val hiltRule = HiltAndroidRule(testClass)

    override fun apply(base: Statement, description: Description): Statement {
        return RuleChain.outerRule(mockWebServer)
            .around(hiltRule)
            .around(composeTestRule)
            .apply(base, description)
    }

    @Before
    fun setUp() {
        composeTestRule.registerIdlingResource(countingResource.toIdlingResource())
        hiltRule.inject()
    }
}
