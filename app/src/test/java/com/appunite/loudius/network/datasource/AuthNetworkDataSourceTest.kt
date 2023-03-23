@file:OptIn(ExperimentalCoroutinesApi::class)

package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.AccessToken
import com.appunite.loudius.network.model.AccessTokenResponse
import com.appunite.loudius.network.services.AuthService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AuthNetworkDataSourceTest {
    private val authService = FakeAuthService()
    private val authNetworkDataSource = AuthNetworkDataSource(authService)

    @Test
    fun `GIVEN correct data WHEN processing THEN return success with new valid token`() =
        runTest {
            val result = authNetworkDataSource.getAccessToken("clientId", "clientSecret", "correct_code")

            Assertions.assertEquals(
                Result.success("validAccessToken"),
                result,
            )
        }

    @Test
    fun `GIVEN incorrect access code WHEN accessing token THEN return failure with BadVerificationCodeException`() =
        runTest {
            val result = authNetworkDataSource.getAccessToken("clientId", "clientSecret", "incorrect_code")

            Assertions.assertEquals(
                Result.failure<AccessToken>(BadVerificationCodeException),
                result,
            )
        }

    @Test
    fun `GIVEN incorrect data WHEN processing THEN return failure with UnknownGithubException`() =
        runTest {
            val result = authNetworkDataSource.getAccessToken("", "", "")

            Assertions.assertEquals(
                Result.failure<AccessToken>(UnknownGithubException),
                result,
            )
        }
}

class FakeAuthService : AuthService {
    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): AccessTokenResponse = when (code) {
        "correct_code" -> AccessTokenResponse("validAccessToken")
        "incorrect_code" -> AccessTokenResponse(null, "bad_verification_code")
        else -> AccessTokenResponse(null, "error")
    }
}
