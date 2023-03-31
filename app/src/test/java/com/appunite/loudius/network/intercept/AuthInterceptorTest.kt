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

import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.network.retrofitTestDouble
import com.appunite.loudius.network.testOkHttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.http.GET

class AuthInterceptorTest {
    private val fakeUserRepository = FakeAuthRepository()
    private val testOkHttpClient = testOkHttpClient(fakeUserRepository)
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
    fun `GIVEN saved token WHEN making an api call THEN authorization token should be added`() {
        runTest {
            val testDataJson = "{\"name\":\"test\"}"
            val successResponse = MockResponse().setBody(testDataJson)
            mockWebServer.enqueue(successResponse)

            service.test()
            val request = mockWebServer.takeRequest()
            val header = request.getHeader("authorization")

            assertEquals("Bearer validToken", header)
        }
    }

    private interface TestApi {

        @GET("/test")
        suspend fun test(): TestData
    }

    private data class TestData(val name: String)
}
