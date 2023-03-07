package com.appunite.loudius.domain

import com.appunite.loudius.network.model.AccessToken

interface AuthRepository {

    suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken>

    fun getAccessToken(): String
}
