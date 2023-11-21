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

import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.robolectric.annotation.Config

@Config(sdk = [28]) // Use the desired Android SDK version.
class FirebaseAnalyticsEventTrackerTest {

    private val firebaseAnalytics: FirebaseAnalytics = mockk()
    private val converter = EventParametersConverter()
    private val eventTracker = FirebaseAnalyticsEventTracker(firebaseAnalytics, converter)

    private val event = object : Event {
        override val name: String = "sample_event"
        override val parameters: List<EventParameter> = emptyList()
    }

    @Before
    fun setUp() {
        mockkStatic(FirebaseAnalytics::class)
        every { FirebaseAnalytics.getInstance(any()) } returns firebaseAnalytics
        every { firebaseAnalytics.logEvent(any(), any()) } returns Unit
    }

    @Test
    fun testTrackEvent() {
        eventTracker.trackEvent(event)

        verify { firebaseAnalytics.logEvent(any(), any()) }
    }
}
