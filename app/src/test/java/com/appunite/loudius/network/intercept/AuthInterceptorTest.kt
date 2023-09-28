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

import com.appunite.loudius.domain.repository.AuthRepository
import com.appunite.loudius.network.httpClientTestDouble
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class AuthInterceptorTest {

    private val mockWebServer: MockWebServer = MockWebServer()

    private lateinit var client: HttpClient
    private lateinit var service: TestApi

    private lateinit var authInterceptor: AuthInterceptor

    @MockK
    private lateinit var authRepository: AuthRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        authInterceptor = AuthInterceptor(authRepository)
        client = httpClientTestDouble(mockWebServer = mockWebServer) { engine { addInterceptor(authInterceptor) } }
        service = TestApi(client)

        mockWebServer.start()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `GIVEN saved token WHEN making an api call THEN authorization token should be added`() {
        runTest {
            every { authRepository.getAccessToken() } returns "validToken"
            val testDataJson = "{\"name\":\"test\"}"
            val successResponse = MockResponse().setBody(testDataJson)
            mockWebServer.enqueue(successResponse.addHeader("Content-type", "application/json"))

            service.test()
            val request = mockWebServer.takeRequest()

            expectThat(request)
                .get("Authorization header") { getHeader("authorization") }
                .isEqualTo("Bearer validToken")
        }
    }

    private class TestApi(private val client: HttpClient) {

        suspend fun test(): Result<TestData> = runCatching { client.get("/test").body() }
    }

    private data class TestData(val name: String)
}
