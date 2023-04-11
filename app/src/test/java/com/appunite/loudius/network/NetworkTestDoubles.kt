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

import com.appunite.loudius.domain.repository.AuthRepository
import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.network.intercept.AuthFailureInterceptor
import com.appunite.loudius.network.intercept.AuthInterceptor
import com.appunite.loudius.network.utils.ApiRequester
import com.appunite.loudius.network.utils.AuthFailureHandler
import com.appunite.loudius.network.utils.AuthFailureHandlerImpl
import com.appunite.loudius.network.utils.LocalDateTimeDeserializer
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun testOkHttpClient(
    authRepository: AuthRepository = FakeAuthRepository(),
    authFailureHandler: AuthFailureHandler = AuthFailureHandlerImpl(),
) = OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.SECONDS)
    .readTimeout(1, TimeUnit.SECONDS)
    .writeTimeout(1, TimeUnit.SECONDS)
    .addInterceptor(AuthInterceptor(authRepository))
    .addInterceptor(AuthFailureInterceptor(authFailureHandler))
    .build()

private fun testGson() =
    GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

fun testRequester() = ApiRequester(testGson())

fun retrofitTestDouble(
    client: OkHttpClient = testOkHttpClient(),
    gson: Gson = testGson(),
    mockWebServer: MockWebServer,
): Retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(mockWebServer.url("/"))
    .build()
