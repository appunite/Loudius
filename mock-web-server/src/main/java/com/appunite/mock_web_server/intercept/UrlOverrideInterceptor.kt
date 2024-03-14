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

package com.appunite.mock_web_server.intercept

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class UrlOverrideInterceptor(private val baseUrl: HttpUrl) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .host(baseUrl.host)
            .scheme(baseUrl.scheme)
            .port(baseUrl.port)
            .build()
        return chain.proceed(
            request.newBuilder().url(newUrl)
                .addHeader("X-Test-Original-Url", request.url.toString()).build()
        )
    }
}
