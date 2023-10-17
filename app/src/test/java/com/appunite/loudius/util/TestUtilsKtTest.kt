package com.appunite.loudius.util

import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.hasLength


class TestUtilsKtTest {

    @Test
    fun verifyOtpIsGenerated() {
        expectThat(generateOtp()).hasLength(6)
    }
}