package com.appunite.loudius.util

import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import strikt.api.Assertion
import strikt.assertions.isNotNull

inline val Assertion.Builder<Request>.url: Assertion.Builder<HttpUrl> get() = get(Request::url)
inline val Assertion.Builder<Request>.method: Assertion.Builder<String> get() = get(Request::method)

@get:JvmName("requestBody")
inline val Assertion.Builder<Request>.body: Assertion.Builder<Buffer> get() = get(Request::body)
inline val Assertion.Builder<Buffer>.utf8: Assertion.Builder<String> get() = get("utf8 string") { readUtf8() }

@get:JvmName("requestHeaders")
inline val Assertion.Builder<Request>.headers: Assertion.Builder<Headers> get() = get(Request::headers)
inline val Assertion.Builder<Response>.code: Assertion.Builder<Int> get() = get("code") { code }

@get:JvmName("responseBody")
inline val Assertion.Builder<Response>.body: Assertion.Builder<ResponseBody?> get() = get("body") { body }
inline val Assertion.Builder<Response>.bodyString: Assertion.Builder<String> get() = body.isNotNull().get("utf8") { string() }

@get:JvmName("responseHeaders")
inline val Assertion.Builder<Response>.headers: Assertion.Builder<Headers> get() = get("headers") { headers }
inline fun Assertion.Builder<Headers>.header(name: String): Assertion.Builder<String?> = get("header $name") { get(name) }
inline val Assertion.Builder<HttpUrl>.path: Assertion.Builder<String> get() = get("path") { this.encodedPath }
inline val Assertion.Builder<HttpUrl>.host: Assertion.Builder<String> get() = get("host") { this.host }

@Suppress("NOTHING_TO_INLINE")
inline fun Assertion.Builder<HttpUrl>.queryParameter(name: String): Assertion.Builder<String?> = get("query parameter \"$name\"") { this.queryParameter(name) }
