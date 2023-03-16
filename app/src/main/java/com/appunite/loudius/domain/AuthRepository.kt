package com.appunite.loudius.domain

import com.appunite.loudius.network.model.AccessTokenResponse

interface AuthRepository {

    suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessTokenResponse>

    fun getAccessToken(): String
}
