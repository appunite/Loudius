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

import com.appunite.loudius.network.httpClientTestDouble
import com.appunite.loudius.network.utils.AuthFailureHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.assertions.isFailure
import strikt.assertions.isSuccess

class AuthFailureInterceptorTest {

    private val mockWebServer: MockWebServer = MockWebServer()

    private lateinit var client: HttpClient
    private lateinit var service: TestApi

    private lateinit var authFailureInterceptor: AuthFailureInterceptor

    @MockK
    private lateinit var authFailureHandler: AuthFailureHandler

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        authFailureInterceptor = AuthFailureInterceptor(authFailureHandler)
        client = httpClientTestDouble(mockWebServer) {
            engine { addInterceptor(authFailureInterceptor) }
        }
        service = TestApi(client)


        mockWebServer.start()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `GIVEN not authorized user WHEN making an api call THEN auth failure should be handled`() =
        runTest {
            val testDataJson = "{\"message\":\"AuthFailureResponse\"}"

            val failureResponse = MockResponse()
                .setResponseCode(401)
                .setBody(testDataJson)
                .addHeader("Content-type", "application/json")
            mockWebServer.enqueue(failureResponse)

            expectCatching { service.makeARequest() }
                .isFailure()
        }

    @Test
    fun `GIVEN authorized user WHEN making an api call THEN auth failure is not emitted`() =
        runTest {
            val testDataJson = "{\"message\":\"successResponse\"}"

            val successResponse = MockResponse()
                .setResponseCode(200)
                .setBody(testDataJson)
                .addHeader("Content-type", "application/json")
            mockWebServer.enqueue(successResponse)

            expectCatching { service.makeARequest() }
                .isSuccess()
            coVerify(exactly = 0) { authFailureHandler.emitAuthFailure() }
        }

    private class TestApi(private val client: HttpClient) {

        suspend fun makeARequest(): TestData = client.get("/test").body()
    }

    private data class TestData(val message: String)
}
