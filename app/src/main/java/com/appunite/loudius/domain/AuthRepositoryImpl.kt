package com.appunite.loudius.domain

import android.util.Log
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
        result
            .onSuccess {
                if (it.accessToken != null) {
                    userLocalDataSource.saveAccessToken(it.accessToken)
                } else {
                    Log.i("failure", it.toString() + "bad_verification_code")
                }
            }
            .onFailure { Log.i("failure", "incorrect_client_credentials") }
        return result
    }

    override fun getAccessToken(): String = userLocalDataSource.getAccessToken()
}
