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
