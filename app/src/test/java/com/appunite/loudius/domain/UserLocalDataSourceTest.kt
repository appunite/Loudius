package com.appunite.loudius.domain

import android.content.Context
import android.content.SharedPreferences
import com.appunite.loudius.domain.store.UserLocalDataSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserLocalDataSourceTest {
    private val sharedPreferences = mockk<SharedPreferences>(relaxed = true) {
        every { getString("access_token", null) } returns "exampleAccessToken"
    }
    private val context = mockk<Context> {
        every { getSharedPreferences(any(), any()) } returns sharedPreferences
    }
    private val userLocalDataSource = UserLocalDataSource(context)

    @Test
    fun `GIVEN filled data source WHEN getting access token THEN return access token`() {
        val result = userLocalDataSource.getAccessToken()
        assertEquals("exampleAccessToken", result) { "Access token should be correct" }
    }

    @Test
    fun `GIVEN not filled data source WHEN getting access token THEN return empty string`() {
        every { sharedPreferences.getString("access_token", null) } returns null

        val result = userLocalDataSource.getAccessToken()
        assertEquals("", result)
    }

    @Test
    fun `GIVEN access token WHEN saving access token THEN shared preferences are edited`() {
        userLocalDataSource.saveAccessToken("exampleAccessToken")

        verify(exactly = 1) {
            sharedPreferences.edit().putString("access_token", "exampleAccessToken")
        }
    }
}
