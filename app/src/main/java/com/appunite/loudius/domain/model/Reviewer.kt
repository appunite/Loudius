package com.appunite.loudius.domain.model

data class Reviewer(
    val id: Int,
    val login: String,
    val isReviewDone: Boolean,
    val hoursFromPRStart: Int,
    val hoursFromReviewDone: Int?,
)
