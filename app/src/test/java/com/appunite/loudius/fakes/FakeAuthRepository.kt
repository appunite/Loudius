package com.appunite.loudius.fakes

import com.appunite.loudius.domain.AuthRepository
import com.appunite.loudius.network.model.AccessToken

class FakeAuthRepository : AuthRepository {
    override suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken> {
        return Result.success("validToken")
    }

    override fun getAccessToken(): String {
        return "validToken"
    }
}
