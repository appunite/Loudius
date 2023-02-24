package com.appunite.loudius.domain

import com.appunite.loudius.network.GithubDataSource
import com.appunite.loudius.network.model.AccessToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val githubDataSource: GithubDataSource,
) : UserRepository {

    override suspend fun getAccessToken(clientId: String, clientSecret: String, code: String): Result<AccessToken> =
        githubDataSource.getAccessToken(clientId, clientSecret, code)
}
