package com.appunite.loudius.domain.model

data class Reviewer(
    val name: String,
    val isReviewDone: Boolean,
    val hoursFromPRStart: Int,
    val hoursFromReviewDone: Int?,
)
