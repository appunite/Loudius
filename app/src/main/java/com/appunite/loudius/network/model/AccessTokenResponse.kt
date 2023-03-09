package com.appunite.loudius.network.model

data class AccessTokenResponse(

    val accessToken: String?,
    val error: String? = null,
)
