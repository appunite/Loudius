package com.appunite.loudius.domain

import com.appunite.loudius.network.UserDataSource
import com.appunite.loudius.network.model.AccessToken
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : UserRepository {

    override suspend fun getAndSaveAccessToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<AccessToken> {
        val tokenFromLocal = userLocalDataSource.getAccessToken()

        return if (tokenFromLocal != null) {
            // TODO: Propose removal of AccessToken data class
            Result.success(AccessToken(tokenFromLocal))
        } else {
            val result = userDataSource.getAccessToken(clientId, clientSecret, code)
            result.onSuccess { userLocalDataSource.saveAccessToken(it.accessToken) }
        }
    }
}
