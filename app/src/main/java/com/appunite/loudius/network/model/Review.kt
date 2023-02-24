package com.appunite.loudius.network.model

import java.time.LocalDateTime

data class Review(
    val id: String,
    val userId: String,
    val state: ReviewState,
    val submittedAt: LocalDateTime
)
