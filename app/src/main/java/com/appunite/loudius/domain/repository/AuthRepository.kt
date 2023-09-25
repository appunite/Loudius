/*
 * Copyright 2023 AppUnite S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appunite.loudius.domain.repository

import com.appunite.loudius.domain.store.UserLocalDataSource
import com.appunite.loudius.network.datasource.AuthDataSource
import com.appunite.loudius.network.model.AccessToken

interface AuthRepository {

    suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken>

    fun getAccessToken(): AccessToken
}

class AuthRepositoryImpl(
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
