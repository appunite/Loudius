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

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.intellij.lang.annotations.Language
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

private const val TAG = "MockWebServerRule"
private val logger = KotlinLogging.logger {}

class Request(
    val headers: Headers,
    val method: String,
    val url: HttpUrl,
    val body: Buffer
) {
    override fun toString(): String =
        "Request(method=$method, url=$url, headers=${headers.joinToString(separator = ",") { (key, value) -> "$key: $value" }})"
}

typealias ResponseGenerator = (Request) -> MockResponse

class MockWebServerRule : TestRule {

    private val dispatcher: MockDispatcher = MockDispatcher()

    fun register(response: ResponseGenerator) = dispatcher.register(response)

    fun clear() = dispatcher.clear()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                MockWebServer().use { server ->
                    server.dispatcher = dispatcher
                    TestInterceptor.testInterceptor = UrlOverrideInterceptor(server.url("/"))
                    logger.info {
                        TAG + "TestInterceptor installed"
                    }
                    try {
                        base.evaluate()
                    } catch (e: Throwable) {
                        if (dispatcher.errors.isEmpty()) {
                            throw e
                        } else {
                            throw MultipleFailuresError(
                                "An test exception occurred, but we also found some not mocked requests",
                                buildList {
                                    add(e)
                                    addAll(dispatcher.errors)
                                }
                            )
                        }
                    } finally {
                        logger.info {
                            TAG + "TestInterceptor uninstalled"
                        }
                        TestInterceptor.testInterceptor = null
                    }
                }
            }
        }
    }
}

fun jsonResponse(@Language("JSON") json: String): MockResponse = MockResponse()
    .addHeader("Content-Type", "application/json")
    .setBody(json.trimIndent())

private class UrlOverrideInterceptor(private val baseUrl: HttpUrl) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .host(baseUrl.host)
            .scheme(baseUrl.scheme)
            .port(baseUrl.port)
            .build()
        return chain.proceed(
            request.newBuilder().url(newUrl)
                .addHeader("X-Test-Original-Url", request.url.toString()).build()
        )
    }
}

private class MockDispatcher : Dispatcher() {

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
            logger.warn {
                TAG + e.message!!
            }
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

class MultipleFailuresError(val heading: String, val failures: List<Throwable>) :
    AssertionError(heading, failures.getOrNull(0)) {
    init {
        require(heading.isNotBlank()) { "Heading should not be blank" }
    }

    override val message: String
        get() = buildString {
            append(heading)
            append(" (")
            append(failures.size).append(" ")
            append(
                when (failures.size) {
                    0 -> "no failures"
                    1 -> "failure"
                    else -> "failures"
                }
            )
            append(")")
            append("\n")

            failures.joinTo(this, separator = "\n") {
                nullSafeMessage(it).lines().joinToString(separator = "\n") { "\t$it" }
            }
        }

    private fun nullSafeMessage(failure: Throwable): String =
        failure.javaClass.name + ": " + failure.message.orEmpty().ifBlank { "<no message>" }
}

