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

import com.appunite.loudius.domain.repository.AuthRepositoryImpl
import com.appunite.loudius.domain.store.UserLocalDataSource
import com.appunite.loudius.fakes.FakeUserLocalDataSource
import com.appunite.loudius.network.datasource.AuthDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {
    private val networkDataSource: AuthDataSource = mockk {
        coEvery {
            getAccessToken(any(), any(), any())
        } returns Result.success("validAccessToken")
    }

    private val localDataSource: UserLocalDataSource = FakeUserLocalDataSource()
    private val repository = AuthRepositoryImpl(networkDataSource, localDataSource)

    @Test
    fun `GIVEN valid code WHEN fetching access token THEN return success with new valid token`() =
        runTest {
            val code = "validCode"

            val result = repository.fetchAccessToken("clientId", "clientSecret", code)

            coVerify(exactly = 1) { networkDataSource.getAccessToken(any(), any(), any()) }
            assertEquals(
                Result.success("validAccessToken"),
                result,
            ) { "Expected success result with valid access token" }
        }

    @Test
    fun `GIVEN valid code WHEN fetching access token THEN THEN new token should be stored`() =
        runTest {
            val code = "validCode"

            repository.fetchAccessToken("clientId", "clientSecret", code)

            val result = repository.getAccessToken()
            assertEquals("validAccessToken", result) { "Expected valid access token" }
        }

    @Test
    fun `GIVEN token stored WHEN getting access token THEN return stored access token`() = runTest {
        localDataSource.saveAccessToken("validAccessToken")

        val result = repository.getAccessToken()

        assertEquals("validAccessToken", result) { "Expected valid access token" }
    }

    @Test
    fun `GIVEN not stored access token WHEN getting access token THEN return empty string`() =
        runTest {
            val result = repository.getAccessToken()

            assertEquals("", result) { "Expected empty string" }
        }
}
