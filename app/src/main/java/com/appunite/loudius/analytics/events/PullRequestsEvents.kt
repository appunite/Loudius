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

interface PullRequestsEvent : Event

object RefreshPullRequestsEvent : PullRequestsEvent {
    override val name: String = "action_start"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "refresh_pull_requests_data")
    )
}

object RefreshPullRequestsSuccessEvent : PullRequestsEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "refresh_pull_requests_data"),
        EventParameter.Boolean("success", true)
    )
}

object RefreshPullRequestsFailureEvent : PullRequestsEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "refresh_pull_requests_data"),
        EventParameter.Boolean("success", false)
    )
}

object FetchPullRequestsEvent : PullRequestsEvent {
    override val name: String = "action_start"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "fetch_pull_requests_data")
    )
}

object FetchPullRequestsSuccessEvent : PullRequestsEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "fetch_pull_requests_data"),
        EventParameter.Boolean("success", true)
    )
}

object FetchPullRequestsFailureEvent : PullRequestsEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "fetch_pull_requests_data"),
        EventParameter.Boolean("success", false)
    )
}

object NavigateToReviewersEvent : PullRequestsEvent {
    override val name: String = "item_click"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "pull_request")
    )
}

object PullRequestsScreenOpenedEvent : PullRequestsEvent {
    override val name: String = "screen_opened"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "pull_requests_screen")
    )
}
