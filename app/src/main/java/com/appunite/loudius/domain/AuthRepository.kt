package com.appunite.loudius.domain

import com.appunite.loudius.network.datasource.AuthDataSource
import com.appunite.loudius.network.model.AccessToken
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {

    suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken>

    fun getAccessToken(): AccessToken
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : AuthRepository {

    override suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken> {
        val result = authDataSource.getAccessToken(clientId, clientSecret, code)
        result.onSuccess {
            userLocalDataSource.saveAccessToken(it)
        }
        return result
    }

    override fun getAccessToken(): AccessToken = userLocalDataSource.getAccessToken()
}
