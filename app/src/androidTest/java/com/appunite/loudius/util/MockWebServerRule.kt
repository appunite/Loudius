package com.appunite.loudius.util

import android.util.Log
import com.appunite.loudius.di.TestInterceptor
import io.mockk.MockKMatcherScope
import io.mockk.every
import io.mockk.mockk
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import org.intellij.lang.annotations.Language
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.assertions.isNotNull

private const val TAG = "MockWebServerRule"

class MockWebServerRule : TestRule {

    val dispatcher: Dispatcher = mockk {
        every { shutdown() } returns Unit
        every { peek() } returns MockResponse().apply { this.socketPolicy = SocketPolicy.KEEP_OPEN }
        every { dispatch(any()) } answers {
            val request = it.invocation.args[0] as RecordedRequest
            Log.w(TAG, "Request is not mocked: ${request.method} ${request.path}")
            MockResponse().setResponseCode(404)
        }
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                MockWebServer().use { server ->
                    server.dispatcher = dispatcher
                    TestInterceptor.testInterceptor = UrlOverrideInterceptor(server.url("/"))
                    Log.v(TAG, "TestInterceptor installed")
                    try {

                    } finally {
                        base.evaluate()
                        Log.v(TAG, "TestInterceptor uninstalled")
                        TestInterceptor.testInterceptor = null
                    }
                }
            }

        }
    }
}

class UrlOverrideInterceptor(private val baseUrl: HttpUrl) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .host(baseUrl.host)
            .scheme(baseUrl.scheme)
            .port(baseUrl.port)
            .build()
        Log.w(TAG, "Overriding url, from: ${request.url} to: $newUrl")
        return chain.proceed(request.newBuilder().url(newUrl)
            .addHeader("X-Test-Original-Url", request.url.toString()).build()
        )
    }

}

fun jsonResponse(@Language("JSON") json: String): MockResponse = MockResponse()
    .addHeader("Content-Type", "application/json")
    .setBody(json)

inline fun <reified T : Any> MockKMatcherScope.matchArg(noinline block: Assertion.Builder<T>.() -> Unit): T {
    return match {
        try {
            expectThat(it, block)
            true
        } catch (e: AssertionError) {
            false
        }
    }
}

@get:JvmName("recordedRequestPath")
inline val Assertion.Builder<RecordedRequest>.path: Assertion.Builder<String> get() = get(RecordedRequest::path).isNotNull()
inline val Assertion.Builder<RecordedRequest>.url: Assertion.Builder<HttpUrl> get() = get(RecordedRequest::requestUrl).isNotNull()
@get:JvmName("httpUrlPath")
inline val Assertion.Builder<HttpUrl>.path: Assertion.Builder<String> get() = get("path") { encodedPath }