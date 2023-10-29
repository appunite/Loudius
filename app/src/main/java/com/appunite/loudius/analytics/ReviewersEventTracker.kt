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

class ReviewersEventTracker(private val analyticsService: AnalyticsService) {

    fun trackNotifySuccess() {
        analyticsService.logEvent(eventName = "action_finished") {
            param("item_name", "notify")
            param("success", true)
        }
    }

    fun trackNotifyFailure() {
        analyticsService.logEvent(eventName = "action_finished") {
            param("item_name", "notify")
            param("success", false)
        }
    }

    fun trackClickNotify() {
        analyticsService.logEvent(eventName = "button_click") {
            param("item_name", "notify")
        }
    }

    fun trackRefreshData() {
        analyticsService.logEvent(eventName = "action_start") {
            param("item_name", "refresh_data")
        }
    }

    fun trackRefreshDataSuccess() {
        analyticsService.logEvent(eventName = "action_finished") {
            param("item_name", "refresh_data")
            param("success", true)
        }
    }

    fun trackRefreshDataFailure() {
        analyticsService.logEvent(eventName = "action_finished") {
            param("item_name", "refresh_data")
            param("success", false)
        }
    }

    fun trackFetchData() {
        analyticsService.logEvent(eventName = "action_start") {
            param("item_name", "fetch_data")
        }
    }

    fun trackFetchDataSuccess() {
        analyticsService.logEvent(eventName = "action_finished") {
            param("item_name", "fetch_data")
            param("success", true)
        }
    }

    fun trackFetchDataFailure() {
        analyticsService.logEvent(eventName = "action_finished") {
            param("item_name", "fetch_data")
            param("success", false)
        }
    }
}
