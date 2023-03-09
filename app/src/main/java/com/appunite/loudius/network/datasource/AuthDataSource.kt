package com.appunite.loudius.network.datasource

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
    ): Result<AccessTokenResponse>
}

@Singleton
class AuthNetworkDataSource @Inject constructor(
    private val authService: AuthService,
) : AuthDataSource {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessTokenResponse> =
        safeApiCall { authService.getAccessToken(clientId, clientSecret, code) }
}
