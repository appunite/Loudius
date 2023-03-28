package com.appunite.loudius.ui.reviewers

data class Reviewer(
    val id: Int,
    val login: String,
    val isReviewDone: Boolean,
    val hoursFromPRStart: Long,
    val hoursFromReviewDone: Long?,
    val isLoading: Boolean = false,
)
