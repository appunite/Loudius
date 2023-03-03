package com.appunite.loudius.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private fun testOkHttpClient() = OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.SECONDS)
    .readTimeout(1, TimeUnit.SECONDS)
    .writeTimeout(1, TimeUnit.SECONDS)
    .build()

private fun testGson() =
    GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

fun retrofitTestDouble(
    client: OkHttpClient = testOkHttpClient(),
    gson: Gson = testGson(),
    mockWebServer: MockWebServer
): Retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(mockWebServer.url("/"))
    .build()
