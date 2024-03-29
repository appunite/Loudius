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

package com.appunite.loudius.analytics

import com.appunite.loudius.di.analyticsModule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

class AnalyticsRule : TestWatcher() {

    val analytics = AnalyticsLogger()

    override fun starting(description: Description) {
        super.starting(description)
        GlobalContext.get().unloadModules(listOf(analyticsModule))
        val fakeAnalyticsModule = module {
            single<EventTracker> { analytics }
        }
        GlobalContext.get().loadModules(listOf(fakeAnalyticsModule))
    }
}
