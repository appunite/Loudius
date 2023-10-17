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

import org.junit.Test
import strikt.api.expectCatching
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isNotNull
import strikt.assertions.isSuccess
import java.lang.Exception

class DescribedKtTest {

    @Test
    fun `test without failure, is success`() {
        expectCatching {
            description("test without failure") {

            }
        }
            .isSuccess()
    }

    @Test
    fun `test with unknown error, is not described`() {
        expectCatching {
            description("test with assertion error") {
                throw Exception("Some error")
            }
        }
            .isFailure()
            .isA<Exception>()
            .get(Exception::message)
            .isEqualTo("Some error")
    }

    @Test
    fun `test with assertion error, is described`() {
        expectCatching {
            description("test with assertion error") {
                throw AssertionError("Some error")
            }
        }
            .isFailure()
            .isA<DescriptionAssertionError>()
            .and {
                get(DescriptionAssertionError::message).isEqualTo("Error in step: \"test with assertion error\"")
                get(DescriptionAssertionError::cause).isNotNull()
                    .get(Throwable::message)
                    .isEqualTo("Some error")
            }
    }
    @Test
    fun `test with multiple descriptions, descriptions are merged`() {
        expectCatching {
            description("first description") {
                description("second description") {
                    throw AssertionError("Some error")
                }
            }
        }
            .isFailure()
            .isA<DescriptionAssertionError>()
            .and {
                get(DescriptionAssertionError::message).isEqualTo("Error in step: \"first description -> second description\"")
                get(DescriptionAssertionError::cause).isNotNull()
                    .get(Throwable::message)
                    .isEqualTo("Some error")
            }
    }
}