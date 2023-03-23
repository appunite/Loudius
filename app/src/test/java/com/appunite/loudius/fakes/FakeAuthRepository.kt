package com.appunite.loudius.fakes

import com.appunite.loudius.domain.AuthRepository
import com.appunite.loudius.network.datasource.BadVerificationCodeException
import com.appunite.loudius.network.datasource.UnknownGithubException
import com.appunite.loudius.network.model.AccessToken

class FakeAuthRepository : AuthRepository {
    override suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken> = when (code) {
        "validCode" -> Result.success("validToken")
        "invalidCode" -> Result.failure(BadVerificationCodeException)
        else -> Result.failure(UnknownGithubException)
    }

    override fun getAccessToken(): String {
        return "validToken"
    }
}
