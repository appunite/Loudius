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

package com.appunite.loudius.domain

import android.content.Context
import com.appunite.loudius.domain.store.UserLocalDataSource
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserLocalDataSourceTest {
    private val sharedPreferences = FakeSharedPreferences()
    private val context = mockk<Context> {
        every { getSharedPreferences(any(), any()) } returns sharedPreferences
    }
    private val userLocalDataSource = UserLocalDataSource(context)

    @Test
    fun `GIVEN the app is started first time WHEN getting access token THEN token is empty`() {
        val result = userLocalDataSource.getAccessToken()

        assertEquals("", result) { "Access token should be empty" }
    }

    @Test
    fun `WHEN token is set THEN token can be retrieved`() {
        userLocalDataSource.saveAccessToken("someToken")

        val result = userLocalDataSource.getAccessToken()
        assertEquals("someToken", result) { "Access token is stored" }
    }

    @Test
    fun `GIVEN token is stored WHEN token is cleared THEN token is cleared`() {
        userLocalDataSource.saveAccessToken("someToken")

        userLocalDataSource.saveAccessToken("")

        val result = userLocalDataSource.getAccessToken()
        assertEquals("", result) { "Access token should be empty" }
    }
}
