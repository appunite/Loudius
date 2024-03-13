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

package com.appunite.loudius.di

import com.appunite.loudius.common.Constants
import com.appunite.loudius.network.intercept.AuthFailureInterceptor
import com.appunite.loudius.network.intercept.AuthInterceptor
import com.appunite.loudius.network.utils.AuthFailureHandler
import com.appunite.mock_web_server.TestInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    single<HttpClient>(named("auth")) {
        HttpClient(OkHttp) {
            expectSuccess = true
            engine {
                addInterceptor(TestInterceptor)
                addInterceptor(get<HttpLoggingInterceptor>())
            }
            defaultRequest {
                url(Constants.AUTH_API_URL)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    single<HttpClient>(named("base")) {
        HttpClient(OkHttp) {
            expectSuccess = true
            engine {
                addInterceptor(TestInterceptor)
                addInterceptor(get<HttpLoggingInterceptor>())
                addInterceptor(get<AuthFailureInterceptor>())
                addInterceptor(get<AuthInterceptor>())
            }
            defaultRequest {
                url(Constants.BASE_API_URL)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    singleOf(::AuthFailureInterceptor)
    singleOf(::AuthInterceptor)
    singleOf(::AuthFailureHandler)
}
