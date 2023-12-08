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

data class AnalyticsLog(
    val eventName: String,
    val parameters: Map<String, Any?>
)

class AnalyticsLogger : EventTracker {

    val log: MutableList<AnalyticsLog> = mutableListOf()

    override fun trackEvent(event: Event) {
        log.add(AnalyticsLog(event.name, convert(event.parameters)))
    }

    private fun convert(parameters: List<EventParameter>): Map<String, Any?> =
        parameters.associate {
            it.name to when (it) {
                is EventParameter.String -> it.value
                is EventParameter.Boolean -> it.value
            }
        }
}
