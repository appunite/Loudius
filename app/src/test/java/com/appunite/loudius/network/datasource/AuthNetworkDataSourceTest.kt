@file:OptIn(ExperimentalCoroutinesApi::class)

package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.AccessToken
import com.appunite.loudius.network.model.AccessTokenResponse
import com.appunite.loudius.network.services.AuthService
import com.appunite.loudius.network.utils.WebException
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
            val result = authNetworkDataSource.getAccessToken("clientId", "clientSecret", "correctCode")

            Assertions.assertEquals(
                Result.success("validAccessToken"),
                result,
            )
        }

    @Test
    fun `GIVEN incorrect access code WHEN accessing token THEN return failure with BadVerificationCodeException`() =
        runTest {
            val result = authNetworkDataSource.getAccessToken("clientId", "clientSecret", "incorrectCode")

            Assertions.assertEquals(
                Result.failure<AccessToken>(BadVerificationCodeException),
                result,
            )
        }

    @Test
    fun `GIVEN incorrect data WHEN processing THEN return failure with UnknownError`() =
        runTest {
            val result = authNetworkDataSource.getAccessToken("", "", "")

            Assertions.assertEquals(
                Result.failure<AccessToken>(WebException.UnknownError(null, "error")),
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
        "correctCode" -> AccessTokenResponse("validAccessToken")
        "incorrectCode" -> AccessTokenResponse(null, "bad_verification_code")
        else -> AccessTokenResponse(null, "error")
    }
}
