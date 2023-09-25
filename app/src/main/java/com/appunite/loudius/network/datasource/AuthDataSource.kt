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

package com.appunite.loudius.network.datasource

import com.appunite.loudius.common.flatMap
import com.appunite.loudius.network.model.AccessToken
import com.appunite.loudius.network.model.AccessTokenResponse
import com.appunite.loudius.network.services.AuthService
import com.appunite.loudius.network.utils.WebException

interface AuthDataSource {

    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken>
}

class AuthDataSourceImpl(
    private val authService: AuthService,
) : AuthDataSource {

    companion object {
        private const val BAD_VERIFICATION_CODE_ERROR = "bad_verification_code"
    }

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<AccessToken> =
        authService.getAccessToken(clientId, clientSecret, code).flatMap { response ->
            if (response.accessToken != null) {
                Result.success(response.accessToken)
            } else {
                Result.failure(response.mapErrorToException())
            }
        }

    private fun AccessTokenResponse.mapErrorToException(): Exception {
        return when (error) {
            BAD_VERIFICATION_CODE_ERROR -> BadVerificationCodeException
            else -> WebException.UnknownError(null, error)
        }
    }
}

/**
 * Thrown during authorization with incorrect verification code.
 */
object BadVerificationCodeException : Exception()
