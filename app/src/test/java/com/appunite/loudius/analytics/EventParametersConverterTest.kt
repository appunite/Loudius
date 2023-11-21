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

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Use the desired Android SDK version.
class EventParametersConverterTest {

    private val converter = EventParametersConverter()

    @Test
    fun testConvert() {
        val result = converter.convert(
            listOf(
                EventParameter.String("param_name1", "string_value1"),
                EventParameter.String("param_name2", "string_value2"),
                EventParameter.Boolean("param_name3", true)
            )
        )

        expectThat(result.getString("param_name1")).isEqualTo("string_value1")
        expectThat(result.getString("param_name2")).isEqualTo("string_value2")
        expectThat(result.getBoolean("param_name3")).isEqualTo(true)
    }
}
