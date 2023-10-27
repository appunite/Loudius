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

package com.appunite.loudius.di

import android.content.Context
import android.content.SharedPreferences
import com.appunite.loudius.appModule
import com.appunite.loudius.util.MainDispatcherExtension
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

@ExtendWith(MainDispatcherExtension::class)
class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {
        val mockContext = mockkClass(Context::class)
        val mockSharedPref = mockkClass(SharedPreferences::class)
        every { mockContext.getSharedPreferences(any(), any()) } returns mockSharedPref

        val mockFirebaseAnalytics = mockk<FirebaseAnalytics>()
        mockkStatic(FirebaseAnalytics::class)
        every { FirebaseAnalytics.getInstance(any()) } returns mockFirebaseAnalytics

        koinApplication {
            modules(
                appModule
            )

            checkModules {
                withInstance(mockContext)
            }
        }
    }
}
