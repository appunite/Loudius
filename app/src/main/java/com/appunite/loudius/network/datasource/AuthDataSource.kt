package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.AccessToken
import com.appunite.loudius.network.services.GithubAuthService
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
    private val authService: GithubAuthService,
) : AuthDataSource {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken> =
        safeApiCall { authService.getAccessToken(clientId, clientSecret, code).accessToken }
}
