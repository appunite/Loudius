package com.appunite.loudius.network.model

data class RequestedReviewer(
    val id: Int,
    val login: String,
    val avatarUrl: String,
)

//reviewer id z Review -> user -> id
