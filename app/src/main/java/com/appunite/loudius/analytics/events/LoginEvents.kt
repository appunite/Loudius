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

interface LoginEvent : Event

object ClickLogInEvent : LoginEvent {
    override val name: String = "button_click"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "log_in")
    )
}

object OpenGithubAuthEvent : LoginEvent {
    override val name: String = "simple_action"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "open_github_auth")
    )
}

object ShowXiaomiPermissionDialogEvent : LoginEvent {
    override val name: String = "simple_action"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "show_xiaomi_permission_dialog")
    )
}

object XiaomiPermissionDialogDismissedEvent : LoginEvent {
    override val name: String = "simple_action"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "xiaomi_permission_dialog_dismissed")
    )
}

object XiaomiPermissionDialogPermissionGrantedEvent : LoginEvent {
    override val name: String = "simple_action"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "xiaomi_permission_dialog_permission_granted")
    )
}

object XiaomiPermissionDialogPermissionAlreadyGrantedEvent : LoginEvent {
    override val name: String = "simple_action"
    override val parameters: List<EventParameter> = listOf(
        EventParameter.String("item_name", "xiaomi_permission_dialog_permission_already_granted")
    )
}
