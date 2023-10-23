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
import com.appunite.loudius.network.utils.LocalDateTimeDeserializer
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.serialization.gson.GsonConverter
import okhttp3.logging.HttpLoggingInterceptor
import java.time.LocalDateTime
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Provides
    @AuthAPI
    fun provideBaseAuthUrl() = Constants.AUTH_API_URL

    @Provides
    @BaseAPI
    fun provideBaseAPIUrl() = Constants.BASE_API_URL

    @Provides
    @Singleton
    @AuthAPI
    fun provideAuthHttpClient(
        gson: Gson,
        @AuthAPI baseUrl: String,
        loggingInterceptor: HttpLoggingInterceptor
    ): HttpClient = HttpClient(OkHttp) {
        expectSuccess = true
        engine {
            addInterceptor(TestInterceptor)
            addInterceptor(loggingInterceptor)
        }
        defaultRequest {
            url(baseUrl)
        }
        install(ContentNegotiation) {
            register(ContentType.Application.Json, GsonConverter(gson))
        }
    }

    @Provides
    @Singleton
    @BaseAPI
    fun provideBaseHttpClient(
        gson: Gson,
        @BaseAPI baseUrl: String,
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        authFailureHandler: AuthFailureHandler
    ): HttpClient = HttpClient(OkHttp) {
        expectSuccess = true
        engine {
            addInterceptor(authInterceptor)
            addInterceptor(TestInterceptor)
            addInterceptor(AuthFailureInterceptor(authFailureHandler))
            addInterceptor(loggingInterceptor)
        }
        defaultRequest {
            url(baseUrl)
        }
        install(ContentNegotiation) {
            register(ContentType.Application.Json, GsonConverter(gson))
        }
    }

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .create()
}
