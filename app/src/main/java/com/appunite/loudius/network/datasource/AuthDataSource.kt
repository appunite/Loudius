package com.appunite.loudius.network.datasource

import com.appunite.loudius.common.flatMap
import com.appunite.loudius.network.model.AccessToken
import com.appunite.loudius.network.model.AccessTokenResponse
import com.appunite.loudius.network.services.AuthService
import com.appunite.loudius.network.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

interface AuthDataSource {

    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken>
}

@Singleton
class AuthNetworkDataSource @Inject constructor(
    private val authService: AuthService,
) : AuthDataSource {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken> =
        safeApiCall { authService.getAccessToken(clientId, clientSecret, code) }
            .flatMap { response ->
                response.accessToken?.let { token -> Result.success(token) }
                    ?: Result.failure(response.mapErrorToException())
            }

    private fun AccessTokenResponse.mapErrorToException() = when (error) {
        "bad_verification_code" -> BadVerificationCodeException
        else -> UnknownGithubException
    }
}

object BadVerificationCodeException : Exception()
object UnknownGithubException : Exception()
