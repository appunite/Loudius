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

package com.appunite.loudius.network.serialize

import com.appunite.loudius.network.utils.InstantSerializer
import junit.framework.TestCase.assertEquals
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.junit.Test

class InstantSerializerTest {

    @Test
    fun testSerialization() {
        val instant: Instant = Clock.System.now()
        val serialized = Json.encodeToString(InstantSerializer, instant)
        val deserialized = Json.decodeFromString(InstantSerializer, serialized)

        assertEquals(instant, deserialized)
    }

    @Test
    fun testDeserialization() {
        val json = """"2023-10-20T12:34:56.789Z""""
        val deserialized = Json.decodeFromString(InstantSerializer, json)
        val expectedInstant = Instant.parse("2023-10-20T12:34:56.789Z")

        assertEquals(expectedInstant, deserialized)
    }
}
