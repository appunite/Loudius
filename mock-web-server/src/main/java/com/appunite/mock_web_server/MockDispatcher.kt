/*
 * Copyright 2024 AppUnite S.A.
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

package com.appunite.mock_web_server

import com.appunite.mock_web_server.util.MultipleFailuresError
import com.appunite.mock_web_server.util.ResponseGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

private const val TAG = "MockDispatcher"
private val logger = KotlinLogging.logger {}

class MockDispatcher : Dispatcher() {

    data class Mock(val response: ResponseGenerator)

    private val mocks: MutableList<Mock> = mutableListOf()
    val errors: MutableList<Throwable> = mutableListOf()

    fun register(response: ResponseGenerator) {
        mocks.add(Mock(response))
    }

    fun clear() {
        mocks.clear()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        try {
            val mockRequest = try {
                Request(
                    url = (
                            request.getHeader("X-Test-Original-Url")
                                ?: throw Exception("No X-Test-Original-Url header, problem with mocker")
                            ).toHttpUrl(),
                    headers = request.headers.newBuilder().removeAll("X-Test-Original-Url").build(),
                    method = request.method ?: throw Exception("Nullable method in the request"),
                    body = request.body
                )
            } catch (e: Exception) {
                throw Exception("Request: $request, is incorrect", e)
            }
            return runMocks(mockRequest)
        } catch (e: Throwable) {
            errors.add(e)
            logger.warn { TAG + e.message!! }
            return MockResponse().setResponseCode(404)
        }
    }

    private fun runMocks(mockRequest: Request): MockResponse {
        val assertionErrors = buildList {
            mocks.forEach {
                try {
                    return it.response(mockRequest)
                } catch (e: AssertionError) {
                    add(e)
                }
            }
        }
        throw MultipleFailuresError(
            "Request: ${mockRequest.method} ${mockRequest.url}, " + if (assertionErrors.isEmpty()) "there are no mocks" else "no mock is matching the request",
            assertionErrors
        )
    }
}
