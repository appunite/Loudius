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

package com.appunite.loudius.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockWebServer

private fun registerJson() = Json { ignoreUnknownKeys = true }

fun httpClientTestDouble(
    mockWebServer: MockWebServer,
    block: HttpClientConfig<OkHttpConfig>.() -> Unit = {}
): HttpClient = HttpClient(OkHttp) {
    block(this)

    expectSuccess = true
    defaultRequest {
        url(
            mockWebServer.url("/").toString()
        )
    }
    install(ContentNegotiation) {
        json(registerJson())
    }
}
