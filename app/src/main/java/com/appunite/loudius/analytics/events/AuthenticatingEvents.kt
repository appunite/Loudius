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

interface AuthenticatingEvent : Event

object AuthenticatingScreenOpenedEvent : AuthenticatingEvent {
    override val name: String = "screen_opened"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("screen_name", "authenticating_screen")
    )
}

object AuthenticationStartedEvent : AuthenticatingEvent {
    override val name: String = "action_start"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "authentication"),
        EventParameter.String("screen_name", "authenticating_screen")
    )
}

object AuthenticationFinishedSuccessEvent : AuthenticatingEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "authentication"),
        EventParameter.Boolean("success", true),
        EventParameter.String("screen_name", "authenticating_screen")
    )
}

data class AuthenticationFinishedFailureEvent(val errorMessage: String) : AuthenticatingEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "authentication"),
        EventParameter.Boolean("success", false),
        EventParameter.String("screen_name", "authenticating_screen"),
        EventParameter.String("error_message", errorMessage)
    )
}

object GetAccessTokenStartedEvent : AuthenticatingEvent {
    override val name: String = "action_start"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "get_access_token"),
        EventParameter.String("screen_name", "authenticating_screen")
    )
}

object GetAccessTokenFinishedSuccessEvent : AuthenticatingEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "get_access_token"),
        EventParameter.Boolean("success", true),
        EventParameter.String("screen_name", "authenticating_screen")
    )
}

data class GetAccessTokenFinishedFailureEvent(val errorMessage: String) : AuthenticatingEvent {
    override val name: String = "action_finished"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "get_access_token"),
        EventParameter.Boolean("success", false),
        EventParameter.String("screen_name", "authenticating_screen"),
        EventParameter.String("error_message", errorMessage)
    )
}
