package com.appunite.loudius.network.model

data class PullRequestsResponse(
    val incompleteResults: Boolean,
    val items: List<PullRequest>,
    val totalCount: Int
)
