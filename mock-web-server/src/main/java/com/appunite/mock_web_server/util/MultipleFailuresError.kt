/*
 * Copyright 2024 AppUnite S.A.
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

package com.appunite.mock_web_server.util

class MultipleFailuresError(private val heading: String, val failures: List<Throwable>) :
    AssertionError(heading, failures.getOrNull(0)) {
    init {
        require(heading.isNotBlank()) { "Heading should not be blank" }
    }

    override val message: String
        get() = buildString {
            append(heading)
            append(" (")
            append(failures.size).append(" ")
            append(
                when (failures.size) {
                    0 -> "no failures"
                    1 -> "failure"
                    else -> "failures"
                }
            )
            append(")")
            append("\n")

            failures.joinTo(this, separator = "\n") {
                nullSafeMessage(it).lines().joinToString(separator = "\n") { "\t$it" }
            }
        }

    private fun nullSafeMessage(failure: Throwable): String =
        failure.javaClass.name + ": " + failure.message.orEmpty().ifBlank { "<no message>" }
}
