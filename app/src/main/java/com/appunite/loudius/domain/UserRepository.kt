package com.appunite.loudius.domain

import com.appunite.loudius.network.model.AccessToken

interface UserRepository {

    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<AccessToken>

    suspend fun saveAccessToken(accessToken: AccessToken)
}
