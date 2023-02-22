package com.appunite.loudius.domain

import com.appunite.loudius.network.model.AccessToken

interface GithubRepository {

    suspend fun getAccessToken(clientId: String, clientSecret: String, code: String): Result<AccessToken>

    suspend fun authorize(): Result<String>
}
