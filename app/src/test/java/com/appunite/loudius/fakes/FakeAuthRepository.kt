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

package com.appunite.loudius.fakes

import com.appunite.loudius.domain.repository.AuthRepository
import com.appunite.loudius.network.model.AccessToken
import com.appunite.loudius.network.utils.WebException

class FakeAuthRepository : AuthRepository {
    override suspend fun fetchAccessToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<AccessToken> = when (code) {
        "validCode" -> Result.success("validToken")
        "invalidCode" -> Result.failure(WebException.BadVerificationCodeException)
        else -> Result.failure(WebException.UnknownError(null, null))
    }

    override fun getAccessToken(): String {
        return "validToken"
    }
}
