package com.appunite.loudius.domain

import com.appunite.loudius.network.UserDataSource
import com.appunite.loudius.network.model.AccessToken
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplTest {
    private val networkDataSource: UserDataSource = mockk {
        coEvery {
            getAccessToken(any(), any(), any())
        } returns Result.success(AccessToken("validAccessToken"))
    }
    private val localDataSource: UserLocalDataSource = mockk {
        every { getAccessToken() } returns "validAccessToken"
        every { saveAccessToken(any()) } returns Unit
    }
    private val repository = UserRepositoryImpl(networkDataSource, localDataSource)

    @Test
    fun `GIVEN fetch access token function WHEN processing THEN return success with new valid token`() =
        runTest {
            val result = repository.fetchAccessToken("clientId", "clientSecret", "code")

            coVerify(exactly = 1) { networkDataSource.getAccessToken(any(), any(), any()) }
            assertEquals(
                Result.success(AccessToken("validAccessToken")),
                result,
            ) { "Expected success result with valid access token" }
        }

    @Test
    fun `GIVEN fetch access token WHEN processing THEN new token should be saved`() =
        runTest {
            repository.fetchAccessToken("clientId", "clientSecret", "code")

            coVerify(exactly = 1) { localDataSource.saveAccessToken(any()) }
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
