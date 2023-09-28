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
import com.appunite.loudius.network.utils.AuthFailureHandler
import com.appunite.loudius.network.utils.LocalDateTimeDeserializer
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.serialization.gson.GsonConverter
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.LocalDateTime

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
                register(ContentType.Application.Json, GsonConverter(get()))
            }
        }
    }

    single<HttpClient>(named("base")) {
        HttpClient(OkHttp) {
            expectSuccess = true
            engine {
                addInterceptor(TestInterceptor)
                addInterceptor(get<HttpLoggingInterceptor>())
            }
            defaultRequest {
                url(Constants.BASE_API_URL)
            }
            install(ContentNegotiation) {
                register(ContentType.Application.Json, GsonConverter(get()))
            }
        }
    }

    single {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .create()
    }

    singleOf(::AuthFailureHandler)
}