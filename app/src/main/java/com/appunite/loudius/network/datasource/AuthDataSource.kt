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
                if (response.accessToken != null) {
                    Result.success(response.accessToken)
                } else {
                    Result.failure(response.mapErrorToException())
                }
            }

    private fun AccessTokenResponse.mapErrorToException(): java.lang.Exception {
        return when (error) {
            BAD_VERIFICATION_CODE_ERROR -> BadVerificationCodeException
            else -> UnknownGithubException
        }
    }

    companion object {
        private const val BAD_VERIFICATION_CODE_ERROR = "bad_verification_code"
    }
}

object BadVerificationCodeException : Exception()
object UnknownGithubException : Exception()
