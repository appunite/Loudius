package com.appunite.loudius.network

import com.appunite.loudius.network.model.AccessToken
import com.appunite.loudius.network.services.GithubApi
import com.appunite.loudius.network.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

interface UserDataSource {

    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken>
}

@Singleton
class UserNetworkDataSource @Inject constructor(
    private val api: GithubApi,
) : UserDataSource {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken> =
        safeApiCall { api.getAccessToken(clientId, clientSecret, code) }
}
