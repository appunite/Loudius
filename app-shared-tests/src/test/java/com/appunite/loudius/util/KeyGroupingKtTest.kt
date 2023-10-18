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
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEmpty

class KeyGroupingKtTest {
    @Test
    fun `empty list`() {
        val grouped = groupKeys(listOf())
        expectThat(grouped).isEmpty()
    }

    @Test
    fun `pass events without arguments`() {
        val grouped = groupKeys(
            listOf(
                mockKeyEvent(1),
                mockKeyEvent(2),
                mockKeyEvent(3)
            )
        )
        expectThat(grouped)
            .containsExactly(
                KeyTypeEvent(listOf(1, 2, 3), 0)
            )
    }

    @Test
    fun `ignore events with no action up`() {
        val grouped = groupKeys(
            listOf(
                mockKeyEvent(1, action = KeyEvent.ACTION_DOWN),
                mockKeyEvent(1, action = KeyEvent.ACTION_UP),
                mockKeyEvent(2, action = KeyEvent.ACTION_DOWN),
                mockKeyEvent(2, action = KeyEvent.ACTION_UP)
            )
        )
        expectThat(grouped)
            .containsExactly(
                KeyTypeEvent(listOf(1, 2), 0)
            )
    }

    @Test
    fun `don't group events with different meta state`() {
        val grouped = groupKeys(
            listOf(
                mockKeyEvent(1, metaState = 1),
                mockKeyEvent(2, metaState = 2),
                mockKeyEvent(3, metaState = 3)
            )
        )
        expectThat(grouped)
            .containsExactly(
                KeyTypeEvent(listOf(1), 1),
                KeyTypeEvent(listOf(2), 2),
                KeyTypeEvent(listOf(3), 3)
            )
    }

    @Test
    fun `group events with the same meta state`() {
        val grouped = groupKeys(
            listOf(
                mockKeyEvent(1, metaState = 1),
                mockKeyEvent(2, metaState = 1),
                mockKeyEvent(3, metaState = 2),
                mockKeyEvent(4, metaState = 2),
                mockKeyEvent(5, metaState = 1)
            )
        )
        expectThat(grouped)
            .containsExactly(
                KeyTypeEvent(listOf(1, 2), 1),
                KeyTypeEvent(listOf(3, 4), 2),
                KeyTypeEvent(listOf(5), 1)
            )
    }

    private fun mockKeyEvent(
        keyCode: Int,
        metaState: Int = 0,
        action: Int = KeyEvent.ACTION_UP
    ): KeyEvent = mockk<KeyEvent> {
        every { getKeyCode() } returns keyCode
        every { getAction() } returns action
        every { getMetaState() } returns metaState
    }
}
