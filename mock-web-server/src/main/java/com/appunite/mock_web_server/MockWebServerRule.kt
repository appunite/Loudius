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

import com.appunite.mock_web_server.intercept.TestInterceptor
import com.appunite.mock_web_server.intercept.UrlOverrideInterceptor
import com.appunite.mock_web_server.util.MultipleFailuresError
import com.appunite.mock_web_server.util.ResponseGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.Interceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

private const val TAG = "MockWebServerRule"
private val logger = KotlinLogging.logger {}

class MockWebServerRule(
    private val interceptor: Interceptor? = null,
    val dispatcher: MockDispatcher = MockDispatcher()
) : TestRule {

    fun register(response: ResponseGenerator) = dispatcher.register(response)

    fun clear() = dispatcher.clear()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                MockWebServer().use { server ->
                    server.dispatcher = dispatcher
                    TestInterceptor.testInterceptor = interceptor ?: UrlOverrideInterceptor(server.url("/"))
                    logger.info { TAG + "TestInterceptor installed" }
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
                        logger.info { TAG + "TestInterceptor uninstalled" }
                        TestInterceptor.testInterceptor = null
                    }
                }
            }
        }
    }
}
