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

package com.appunite.loudius.rules

import com.pinterest.ktlint.test.KtLintAssertThat
import org.junit.Test

class DoNotMixJunitVersionsTest {

    private val wrappingRuleAssertThat =
        KtLintAssertThat.assertThatRule { DoNotMixJunitVersions() }

    @Test
    fun `allows standard imports`() {
        //language=kotlin
        val code =
            """
            import a.b.c
            import foo.bar
            """.trimIndent()

        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `allows Junit4 imports`() {
        //language=kotlin
        val code =
            """
            import org.junit.Test
            import org.junit.Before
            import org.junit.After
            import org.junit.Ignore
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized
            import org.junit.runners.Theor
            """.trimIndent()

        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `allows Junit5 imports`() {
        //language=kotlin
        val code =
            """
            import org.junit.jupiter.api.Test
            import org.junit.jupiter.api.BeforeEach
            import org.junit.jupiter.api.AfterEach
            import org.junit.jupiter.api.Disabled
            import org.junit.jupiter.api.extension.ExtendWith
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.ValueSourc
            """.trimIndent()

        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `fail if Junit4 is mixed with Junit5`() {
        //language=kotlin
        val code =
            """
            import org.junit.Test
            import org.junit.jupiter.api.Test
            """.trimIndent()

        wrappingRuleAssertThat(code)
            .hasLintViolationWithoutAutoCorrect(
                1,
                1,
                "org.junit.Test and org.junit.jupiter.api.Test packages are from different JUnit versions. Don't mix Junit4 with Junit5 in a single test.",
            )
    }

    @Test
    fun `fail if multiple Junit4 is mixed with Junit5`() {
        //language=kotlin
        val code =
            """
            import org.junit.Test
            import org.junit.Before
            import org.junit.jupiter.api.Test
            import org.junit.jupiter.api.BeforeEach
            """.trimIndent()

        wrappingRuleAssertThat(code)
            .hasLintViolationWithoutAutoCorrect(
                1,
                1,
                "org.junit.Test,org.junit.Before and org.junit.jupiter.api.Test,org.junit.jupiter.api.BeforeEach packages are from different JUnit versions. Don't mix Junit4 with Junit5 in a single test.",
            )
    }
}
