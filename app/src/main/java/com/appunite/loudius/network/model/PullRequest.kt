package com.appunite.loudius.network.model

import com.appunite.loudius.common.Constants

data class PullRequest(
    val id: Int,
    val draft: Boolean,
    val number: Int,
    val repositoryUrl: String,
    val title: String,
    val updatedAt: String,
) {
    val fullRepositoryName: String
        get() = repositoryUrl.removePrefix(Constants.BASE_API_URL + "/")
}
