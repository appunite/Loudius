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

package com.appunite.loudius

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.marcinziolo.kotlin.wiremock.get
import com.marcinziolo.kotlin.wiremock.like
import com.marcinziolo.kotlin.wiremock.returns
import junit.framework.TestCase.assertEquals
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.ServerSocket

class GithubApiTest {

    private val port = findRandomPort()

    @Rule
    @JvmField
    var wiremock: WireMockRule = WireMockRule(port)

    @Before
    fun setup() {
        wiremock.start()
    }

    @After
    fun teardown() {
        wiremock.stop()
    }

    @Test
    fun testGetPullRequests() {
        // Setup a stub for the API endpoint
        val owner = "appunite"
        val repo = "Loudius"
        val path = "/repos/$owner/$repo/pulls"
        val response = """[
            {"title": "test"}
        ]"""

        wiremock.get {
            url like path
        } returns {
            aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(response)
        }

        // Make the API request
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(wiremock.baseUrl() + path)
            .build()
        val responseServer = client.newCall(request).execute()

        // Verify the response
        assertEquals(responseServer.isSuccessful, true)
        assertEquals(responseServer.body?.string(), response)
    }

    private fun findRandomPort(): Int {
        ServerSocket(0).use { socket -> return socket.localPort }
    }
}
