@file:OptIn(ExperimentalCoroutinesApi::class)

package com.appunite.loudius.network

import com.appunite.loudius.fakes.FakeUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.http.GET

class AuthInterceptorTest {
    private val fakeUserRepository = FakeUserRepository()
    private val testOkHttpClient = testOkHttpClient(fakeUserRepository)
    private val mockWebServer: MockWebServer = MockWebServer()
    private val service = retrofitTestDouble(
        mockWebServer = mockWebServer, client = testOkHttpClient
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
