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

package com.appunite.loudius.network.services

import com.appunite.loudius.network.model.AccessTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters

interface AuthService {

    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<AccessTokenResponse>
}

class AuthServiceImpl(private val client: HttpClient) : AuthService {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<AccessTokenResponse> = runCatching {
        client.submitForm(
            url = "login/oauth/access_token",
            formParameters = parameters {
                append("client_id", clientId)
                append("client_secret", clientSecret)
                append("code", code)
            }
        ) {
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }.body()
    }
}
