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

package com.appunite.loudius.util

import android.view.KeyEvent


data class KeyTypeEvent(val keyCodes: List<Int>, val metaState: Int)

/**
 * Optimize typing by grouping characters with the same metaState
 *
 * UiDevice has UiDevice.pressKeyCode and UiDevice.pressKeyCodes methods.
 * Using UiDevice.pressKeyCodes is much faster for typing multiple characters, but
 * it couldn't be used for characters with a different metaState (e.g. Alt/Shift/Ctr).
 * i.e. to write "Jacek Marchwicki" we need to use:
 * * pressKeyCodes "j" with Shift
 * * pressKeyCodes "acek " without metaState
 * * pressKeyCodes "m" with Shift
 * * pressKeyCodes "archwicki " without metaState
 *
 * so instead of calling "pressKeyCode" 16 times, we call "pressKeyCodes" 4 times - this is
 * significantly faster.
 *
 * This method groups those calls, so we can type faster.
 */
fun groupKeys(list: List<KeyEvent>): List<KeyTypeEvent> = list
    .filter { it.action == KeyEvent.ACTION_UP }
    .fold(listOf()) { acc, event ->
        val last = acc.lastOrNull()
        if (last != null && last.metaState == event.metaState) {
            acc.dropLast(1).plus(KeyTypeEvent(last.keyCodes.plus(event.keyCode), event.metaState))
        } else {
            acc.plus(KeyTypeEvent(listOf(event.keyCode), event.metaState))
        }
    }
