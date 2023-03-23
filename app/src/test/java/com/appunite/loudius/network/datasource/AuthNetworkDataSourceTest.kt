@file:OptIn(ExperimentalCoroutinesApi::class)

package com.appunite.loudius.network.datasource

import com.appunite.loudius.fakes.FakeAuthRepository
import com.appunite.loudius.network.model.AccessToken
import com.appunite.loudius.network.retrofitTestDouble
import com.appunite.loudius.network.services.AuthService
import com.appunite.loudius.network.testOkHttpClient
import com.appunite.loudius.network.utils.WebException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AuthNetworkDataSourceTest {
    private val fakeUserRepository = FakeAuthRepository()
    private val testOkHttpClient = testOkHttpClient(fakeUserRepository)
    private val mockWebServer: MockWebServer = MockWebServer()
    private val authService = retrofitTestDouble(
        mockWebServer = mockWebServer,
        client = testOkHttpClient,
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
                MockResponse().setResponseCode(200).setBody(jsonResponse),
            )

            val result =
                authNetworkDataSource.getAccessToken("clientId", "clientSecret", "correctCode")

            Assertions.assertEquals(
                Result.success("validAccessToken"),
                result,
            )
        }

    @Test
    fun `GIVEN incorrect access code WHEN accessing token THEN return failure with BadVerificationCodeException`() =
        runTest {
            //language=JSON
            val jsonResponse = """
            { "error": "bad_verification_code" }
        """.trimIndent()

            mockWebServer.enqueue(
                MockResponse().setResponseCode(200).setBody(jsonResponse),
            )

            val result = authNetworkDataSource.getAccessToken("clientId", "clientSecret", "incorrectCode")

            Assertions.assertEquals(
                Result.failure<AccessToken>(BadVerificationCodeException),
                result,
            )
        }

    @Test
    fun `GIVEN incorrect data WHEN accessing token THEN return failure with UnknownError`() =
        runTest {
            //language=JSON
            val jsonResponse = """
            { "error": "error" }
        """.trimIndent()

            mockWebServer.enqueue(
                MockResponse().setResponseCode(200).setBody(jsonResponse),
            )

            val result = authNetworkDataSource.getAccessToken("", "", "")

            Assertions.assertEquals(
                Result.failure<AccessToken>(WebException.UnknownError(null, "error")),
                result,
            )
        }
}
