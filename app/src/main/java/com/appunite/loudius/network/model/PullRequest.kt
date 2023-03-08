package com.appunite.loudius.network.model

import com.appunite.loudius.common.Constants
import java.time.LocalDateTime

data class PullRequest(
    val id: Int,
    val draft: Boolean,
    val number: Int,
    val repositoryUrl: String,
    val title: String,
    val updatedAt: LocalDateTime,
) {
    val fullRepositoryName: String
        get() = repositoryUrl.removePrefix(REPOSITORY_PATH)

    companion object {
        private const val REPOSITORY_PATH = Constants.BASE_API_URL + "/repos/"
    }
}
