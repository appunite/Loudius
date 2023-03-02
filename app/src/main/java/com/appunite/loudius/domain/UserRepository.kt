package com.appunite.loudius.domain

import com.appunite.loudius.network.model.AccessToken

interface UserRepository {

    suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken>

    fun getAccessToken(): String
}
