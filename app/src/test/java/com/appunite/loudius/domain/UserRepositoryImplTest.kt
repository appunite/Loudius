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
        every { getAccessToken() } returns null
        every { saveAccessToken(any()) } returns Unit
    }
    private val repository = UserRepositoryImpl(networkDataSource, localDataSource)

    @Test
    fun `GIVEN not saved token locally WHEN getting token THEN return new token from network`() =
        runTest {
            val result = repository.getAndSaveAccessToken("clientId", "clientSecret", "code")

            coVerify(exactly = 1) { networkDataSource.getAccessToken(any(), any(), any()) }
            assertEquals(
                Result.success(AccessToken("validAccessToken")),
                result,
            ) { "Expected success result with valid access token" }
        }

    @Test
    fun `GIVEN not saved token locally WHEN getting token THEN new token should be saved`() =
        runTest {
            repository.getAndSaveAccessToken("clientId", "clientSecret", "code")

            coVerify(exactly = 1) { localDataSource.saveAccessToken(any()) }
        }

    @Test
    fun `GIVEN saved token locally WHEN getting token THEN return saved token`() = runTest {
        every { localDataSource.getAccessToken() } returns "validAccessToken"

        val result = repository.getAndSaveAccessToken("clientId", "clientSecret", "code")

        coVerify(exactly = 0) { networkDataSource.getAccessToken(any(), any(), any()) }
        assertEquals(
            Result.success(AccessToken("validAccessToken")),
            result,
        ) { "Expected success result with valid access token" }
    }
}
