package com.appunite.loudius.network.model

typealias AccessToken = String

data class AccessTokenResponse(
    val accessToken: AccessToken?,
    val error: String? = null,
)
