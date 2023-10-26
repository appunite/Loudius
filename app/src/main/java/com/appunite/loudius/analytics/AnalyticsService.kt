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

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

interface AnalyticsService {

    fun logEvent(eventName: String, param: String, value: String)
}

class AnalyticsServiceImpl(private val firebaseAnalytics: FirebaseAnalytics) : AnalyticsService {

    override fun logEvent(eventName: String, param: String, value: String) {
        firebaseAnalytics.logEvent(eventName, createBundle(param, value))
    }

    private fun createBundle(key: String, value: String): Bundle =
        Bundle().apply { putString(key, value) }
}
