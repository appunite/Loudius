package com.appunite.loudius.network.model

import java.time.LocalDateTime

data class Review(
    val id: String,
    val user: User,
    val state: ReviewState,
    val submittedAt: LocalDateTime,
)

data class User(
    val id: Int,
)
