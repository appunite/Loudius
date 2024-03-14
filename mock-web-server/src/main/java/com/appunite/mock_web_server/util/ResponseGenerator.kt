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

import com.appunite.mock_web_server.Request
import okhttp3.mockwebserver.MockResponse
import org.intellij.lang.annotations.Language

typealias ResponseGenerator = (Request) -> MockResponse

fun jsonResponse(@Language("JSON") json: String): MockResponse = MockResponse()
    .addHeader("Content-Type", "application/json")
    .setBody(json.trimIndent())
