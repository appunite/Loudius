package com.appunite.loudius.domain

import com.appunite.loudius.network.UserDataSource
import com.appunite.loudius.network.model.AccessToken
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : UserRepository {

    override suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken> {
        val result = userDataSource.getAccessToken(clientId, clientSecret, code)
        result.onSuccess { userLocalDataSource.saveAccessToken(it.accessToken) }
        return result
    }

    override fun getAccessToken(): String = userLocalDataSource.getAccessToken()
}
