package com.appunite.loudius.domain

import com.appunite.loudius.network.datasource.AuthDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
    private val localDataSource: UserLocalDataSource = mockk {
        every { getAccessToken() } returns "validAccessToken"
        every { saveAccessToken(any()) } returns Unit
    }
    private val repository = AuthRepositoryImpl(networkDataSource, localDataSource)

    @Test
    fun `GIVEN fetch access token function WHEN processing THEN return success with new valid token`() =
        runTest {
            val result = repository.fetchAccessToken("clientId", "clientSecret", "validCode")

            coVerify(exactly = 1) { networkDataSource.getAccessToken(any(), any(), any()) }
            assertEquals(
                Result.success("validAccessToken"),
                result,
            ) { "Expected success result with valid access token" }
        }

    @Test
    fun `GIVEN fetch access token WHEN processing THEN new token should be saved`() =
        runTest {
            repository.fetchAccessToken("clientId", "clientSecret", "validCode")

            coVerify(exactly = 1) { localDataSource.saveAccessToken("validAccessToken") }
        }

    @Test
    fun `GIVEN token stored WHEN getting access token THEN return stored access token`() = runTest {
        val result = repository.getAccessToken()

        assertEquals("validAccessToken", result) { "Expected valid access token" }
    }

    @Test
    fun `GIVEN not stored access token WHEN getting access token THEN return empty string`() =
        runTest {
            every { repository.getAccessToken() } returns ""

            val result = repository.getAccessToken()

            assertEquals("", result) { "Expected empty string" }
        }
}
