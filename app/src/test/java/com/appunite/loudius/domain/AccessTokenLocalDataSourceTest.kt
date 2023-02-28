package com.appunite.loudius.domain

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class AccessTokenLocalDataSourceTest {
    private val sharedPreferences = mockk<SharedPreferences>(relaxed = true) {
        every { getString("access_token", null) } returns "exampleAccessToken"
    }
    private val context = mockk<Context> {
        every { getSharedPreferences(any(), any()) } returns sharedPreferences
    }
    private val accessTokenLocalDataSource = AccessTokenLocalDataSource(context)

    @Test
    fun `GIVEN filled data source WHEN getting access token THEN return access token`() {
        val result = accessTokenLocalDataSource.getAccessToken()
        assertEquals("exampleAccessToken", result) { "Access token should be correct" }
    }

    @Test
    fun `GIVEN not filled data source WHEN getting access token THEN return null`() {
        every { sharedPreferences.getString("access_token", null) } returns null

        val result = accessTokenLocalDataSource.getAccessToken()
        assertEquals(null, result)
    }
}
