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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.appunite.loudius.network.intercept

import com.appunite.loudius.network.retrofitTestDouble
import com.appunite.loudius.network.testOkHttpClient
import com.appunite.loudius.network.utils.AuthFailureHandler
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.http.GET
import strikt.api.expectCatching
import strikt.assertions.isA
import strikt.assertions.isFailure
import strikt.assertions.isSuccess

class AuthFailureInterceptorTest {
    private val fakeAuthFailureHandler: AuthFailureHandler = mockk(relaxed = true)
    private val testOkHttpClient = testOkHttpClient(authFailureHandler = fakeAuthFailureHandler)
    private val mockWebServer: MockWebServer = MockWebServer()
    private val service = retrofitTestDouble(
        mockWebServer = mockWebServer,
        client = testOkHttpClient,
    ).create(TestApi::class.java)

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `GIVEN not authorized user WHEN making an api call THEN auth failure should be handled`() =
        runTest {
            val testDataJson = "{\"message\":\"AuthFailureResponse\"}"
            val failureResponse =
                MockResponse().setResponseCode(401).setBody(testDataJson)
            mockWebServer.enqueue(failureResponse)

            expectCatching { service.makeARequest() }
                .isFailure()
                .isA<HttpException>()
            coVerify(exactly = 1) { fakeAuthFailureHandler.emitAuthFailure() }
        }

    @Test
    fun `GIVEN authorized user WHEN making an api call THEN auth failure is not emitted`() =
        runTest {
            val testDataJson = "{\"message\":\"successResponse\"}"
            val successResponse =
                MockResponse().setResponseCode(200).setBody(testDataJson)
            mockWebServer.enqueue(successResponse)

            expectCatching { service.makeARequest() }
                .isSuccess()
            coVerify(exactly = 0) { fakeAuthFailureHandler.emitAuthFailure() }
        }

    private interface TestApi {

        @GET("/test")
        suspend fun makeARequest(): TestData
    }

    private data class TestData(val message: String)
}
