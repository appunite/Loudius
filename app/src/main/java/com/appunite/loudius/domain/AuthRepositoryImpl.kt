package com.appunite.loudius.domain

import com.appunite.loudius.network.datasource.AuthDataSource
import com.appunite.loudius.network.model.AccessTokenResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : AuthRepository {

    override suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessTokenResponse> {
        val result = authDataSource.getAccessToken(clientId, clientSecret, code)
        result.onSuccess { response ->
            response.accessToken?.let {
                userLocalDataSource.saveAccessToken(it)
            }
        }
        return result
    }

    override fun getAccessToken(): String = userLocalDataSource.getAccessToken()
}
