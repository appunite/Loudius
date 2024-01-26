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

package com.appunite.loudius.analytics.events

import com.appunite.loudius.analytics.Event
import com.appunite.loudius.analytics.EventParameter

interface ReviewersEvent : Event

object ReviewersEvents {

    object ScreenOpened : ReviewersEvent {
        override val name: String = "screen_opened"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("screen_name", "reviewers_screen")
        )
    }

    object ClickNotify : ReviewersEvent {
        override val name: String = "button_click"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "notify"),
            EventParameter.String("screen_name", "reviewers_screen")
        )
    }

    object Notify : ReviewersEvent {
        override val name: String = "action_start"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "notify"),
            EventParameter.String("screen_name", "reviewers_screen")
        )
    }

    object NotifySuccess : ReviewersEvent {
        override val name: String = "action_finished"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "notify"),
            EventParameter.Boolean("success", true),
            EventParameter.String("screen_name", "reviewers_screen")
        )
    }

    data class NotifyFailure(val errorMessage: String) : ReviewersEvent {
        override val name: String = "action_finished"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "notify"),
            EventParameter.Boolean("success", false),
            EventParameter.String("screen_name", "reviewers_screen"),
            EventParameter.String("error_message", errorMessage)
        )
    }

    object Refresh : ReviewersEvent {
        override val name: String = "action_start"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "refresh_reviewers_data"),
            EventParameter.String("screen_name", "reviewers_screen")
        )
    }

    object RefreshSuccess : ReviewersEvent {
        override val name: String = "action_finished"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "refresh_reviewers_data"),
            EventParameter.Boolean("success", true),
            EventParameter.String("screen_name", "reviewers_screen")
        )
    }

    data class RefreshFailure(val errorMessage: String) : ReviewersEvent {
        override val name: String = "action_finished"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "refresh_reviewers_data"),
            EventParameter.Boolean("success", false),
            EventParameter.String("screen_name", "reviewers_screen"),
            EventParameter.String("error_message", errorMessage)
        )
    }

    object Fetch : ReviewersEvent {
        override val name: String = "action_start"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "fetch_reviewers_data"),
            EventParameter.String("screen_name", "reviewers_screen")
        )
    }

    object FetchSuccess : ReviewersEvent {
        override val name: String = "action_finished"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "fetch_reviewers_data"),
            EventParameter.Boolean("success", true),
            EventParameter.String("screen_name", "reviewers_screen")
        )
    }

    data class FetchFailure(val errorMessage: String) : ReviewersEvent {
        override val name: String = "action_finished"
        override val parameters: List<EventParameter> = listOf(
            EventParameter.String("item_name", "fetch_reviewers_data"),
            EventParameter.Boolean("success", false),
            EventParameter.String("screen_name", "reviewers_screen"),
            EventParameter.String("error_message", errorMessage)
        )
    }
}
