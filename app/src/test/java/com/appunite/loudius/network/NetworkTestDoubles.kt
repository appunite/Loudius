package com.appunite.loudius.network

import com.appunite.loudius.domain.UserRepository
import com.appunite.loudius.fakes.FakeUserRepository
import com.appunite.loudius.network.utils.AuthInterceptor
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

fun testOkHttpClient(userRepository: UserRepository = FakeUserRepository()) =
    OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor(userRepository))
        .build()

private fun testGson() =
    GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

fun retrofitTestDouble(
    client: OkHttpClient = testOkHttpClient(),
    gson: Gson = testGson(),
    mockWebServer: MockWebServer,
): Retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(mockWebServer.url("/"))
    .build()
