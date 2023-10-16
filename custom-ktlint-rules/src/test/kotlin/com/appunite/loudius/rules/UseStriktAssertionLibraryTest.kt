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

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.Test

class UseStriktAssertionLibraryTest {

    private val wrappingRuleAssertThat = assertThatRule { UseStriktAssertionLibrary() }

    @Test
    fun `do not allow TestCase library`() {
        //language=kotlin
        val code =
            """
            import a.b.c
            import junit.framework.TestCase.assertEquals
            import foo.bar
            """.trimIndent()

        wrappingRuleAssertThat(code)
            .hasLintViolationWithoutAutoCorrect(2, 1, "Instead of using junit.framework.TestCase use strikt.api.expectThat")
    }

    @Test
    fun `do not allow jupiter assertions library`() {
        //language=kotlin
        val code =
            """
            import a.b.c
            import org.junit.jupiter.api.Assertions.assertEquals
            import foo.bar
            """.trimIndent()

        wrappingRuleAssertThat(code)
            .hasLintViolationWithoutAutoCorrect(2, 1, "Instead of using org.junit.jupiter.api.Assertions use strikt.api.expectThat")
    }

    @Test
    fun `do not allow junit assertions library`() {
        //language=kotlin
        val code =
            """
            import a.b.c
            import org.junit.Assert.assertEquals
            import foo.bar
            """.trimIndent()

        wrappingRuleAssertThat(code)
            .hasLintViolationWithoutAutoCorrect(2, 1, "Instead of using org.junit.Assert use strikt.api.expectThat")
    }

    @Test
    fun `do not allow junit assertions framework`() {
        //language=kotlin
        val code =
            """
            import a.b.c
            import junit.framework.Assert.assertEquals
            import foo.bar
            """.trimIndent()

        wrappingRuleAssertThat(code)
            .hasLintViolationWithoutAutoCorrect(2, 1, "Instead of using junit.framework.Assert use strikt.api.expectThat")
    }

    @Test
    fun `allows using skrt library`() {
        //language=kotlin
        val code =
            """
            import a.b.c
            import strikt.api.expectThat
            import foo.bar
            """.trimIndent()

        wrappingRuleAssertThat(code).hasNoLintViolations()
    }
}
