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

package com.appunite.loudius.network.datasource

import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.network.retrofitTestDouble
import com.appunite.loudius.network.services.AuthService
import com.appunite.loudius.network.testOkHttpClient
import com.appunite.loudius.network.utils.WebException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isSuccess

@OptIn(ExperimentalCoroutinesApi::class)
class AuthNetworkDataSourceTest {
    private val fakeUserRepository = FakeAuthRepository()
    private val testOkHttpClient = testOkHttpClient(fakeUserRepository)
    private val mockWebServer: MockWebServer = MockWebServer()
    private val authService = retrofitTestDouble(
        mockWebServer = mockWebServer,
        client = testOkHttpClient
    ).create(AuthService::class.java)

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    private val authNetworkDataSource = AuthNetworkDataSource(authService)

    @Test
    fun `GIVEN correct data WHEN accessing token THEN return success with new valid token`() =
        runTest {
            //language=JSON
            val jsonResponse = """
            { "access_token": "validAccessToken" }
            """.trimIndent()

            mockWebServer.enqueue(
                MockResponse().setResponseCode(200).setBody(jsonResponse)
            )

            val result =
                authNetworkDataSource.getAccessToken("clientId", "clientSecret", "correctCode")

            expectThat(result)
                .isSuccess()
                .isEqualTo("validAccessToken")
        }

    @Test
    fun `GIVEN incorrect access code WHEN accessing token THEN return failure with BadVerificationCodeException`() =
        runTest {
            //language=JSON
            val jsonResponse = """
            { "error": "bad_verification_code" }
            """.trimIndent()

            mockWebServer.enqueue(
                MockResponse().setResponseCode(200).setBody(jsonResponse)
            )

            val result = authNetworkDataSource.getAccessToken("clientId", "clientSecret", "incorrectCode")

            expectThat(result)
                .isFailure()
                .isEqualTo(WebException.BadVerificationCodeException)
        }

    @Test
    fun `GIVEN incorrect data WHEN accessing token THEN return failure with UnknownError`() =
        runTest {
            //language=JSON
            val jsonResponse = """
            { "error": "error" }
            """.trimIndent()

            mockWebServer.enqueue(
                MockResponse().setResponseCode(200).setBody(jsonResponse)
            )

            val result = authNetworkDataSource.getAccessToken("", "", "")

            expectThat(result)
                .isFailure()
                .isEqualTo(WebException.UnknownError(null, "error"))
        }
}
