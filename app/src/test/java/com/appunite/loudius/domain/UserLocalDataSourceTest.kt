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

import com.appunite.loudius.domain.store.EncryptedPrefs
import com.appunite.loudius.domain.store.UserLocalDataSourceImpl
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

class UserLocalDataSourceTest {
    private val encryptedPrefs = mockk<EncryptedPrefs> {
        every { saveAccessToken(any()) } returns Unit
        every { getAccessToken() } returns ""
    }
    private val userLocalDataSource = UserLocalDataSourceImpl(encryptedPrefs)

    @Test
    fun `GIVEN the app is started first time WHEN getting access token THEN token is empty`() {
        every { encryptedPrefs.getAccessToken() } returns ""

        val result = userLocalDataSource.getAccessToken()

        expectThat(result).isEmpty()
    }

    @Test
    fun `WHEN token is set THEN token can be retrieved`() {
        every { encryptedPrefs.getAccessToken() } returns "someToken"

        userLocalDataSource.saveAccessToken("someToken")

        val result = userLocalDataSource.getAccessToken()
        expectThat(result).isEqualTo("someToken")
    }

    @Test
    fun `GIVEN token is stored WHEN token is cleared THEN no token to retrieve`() {
        userLocalDataSource.saveAccessToken("someToken")
        userLocalDataSource.saveAccessToken("")

        val result = userLocalDataSource.getAccessToken()
        expectThat(result).isEmpty()
    }
}
