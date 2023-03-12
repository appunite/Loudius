package com.appunite.loudius.fakes

import com.appunite.loudius.domain.AuthRepository
import com.appunite.loudius.network.model.AccessTokenResponse

class FakeAuthRepository : AuthRepository {
    override suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessTokenResponse> {
        return Result.success(AccessTokenResponse("validToken"))
    }

    override fun getAccessToken(): String {
        return "validToken"
    }
}
