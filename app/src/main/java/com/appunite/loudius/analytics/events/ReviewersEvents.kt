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

object ReviewersScreenOpenedEvent : ReviewersEvent {
    override val name: String = "screen_opened"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object ClickNotifyEvent : ReviewersEvent {
    override val name: String = "button_click"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "notify"),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object NotifyEvent : ReviewersEvent {
    override val name: String = "action_start"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "notify"),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object NotifySuccessEvent : ReviewersEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "notify"),
        EventParameter.Boolean("success", true),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object NotifyFailureEvent : ReviewersEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "notify"),
        EventParameter.Boolean("success", false),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object RefreshReviewersEvent : ReviewersEvent {
    override val name: String = "action_start"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "refresh_reviewers_data"),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object RefreshReviewersSuccessEvent : ReviewersEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "refresh_reviewers_data"),
        EventParameter.Boolean("success", true),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object RefreshReviewersFailureEvent : ReviewersEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "refresh_reviewers_data"),
        EventParameter.Boolean("success", false),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object FetchReviewersEvent : ReviewersEvent {
    override val name: String = "action_start"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "fetch_reviewers_data"),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object FetchReviewersSuccessEvent : ReviewersEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "fetch_reviewers_data"),
        EventParameter.Boolean("success", true),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}

object FetchReviewersFailureEvent : ReviewersEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "fetch_reviewers_data"),
        EventParameter.Boolean("success", false),
        EventParameter.String("screen_name", "reviewers_screen")
    )
}
