package com.appunite.loudius.domain

import com.appunite.loudius.network.GithubDataSource
import com.appunite.loudius.network.model.AccessToken
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val githubDataSource: GithubDataSource,
    private val accessTokenLocalDataSource: AccessTokenLocalDataSource,
) : UserRepository {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<AccessToken> {
        val tokenFromLocal = accessTokenLocalDataSource.getAccessToken()

        return if (tokenFromLocal != null) {
            // TODO: Propose removal of AccessToken data class
            Result.success(AccessToken(tokenFromLocal))
        } else {
            githubDataSource.getAccessToken(clientId, clientSecret, code)

        }
    }

    override suspend fun saveAccessToken(accessToken: AccessToken) {
        accessTokenLocalDataSource.saveAccessToken(accessToken.accessToken)
    }
}
