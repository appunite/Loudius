package com.appunite.loudius.network

import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.utils.safeApiCall
import javax.inject.Inject

const val auth_token = "BEARER xxxxxxx" // temporary solution

class GitHubPullRequestsDataSource @Inject constructor(private val service: GithubPullRequestsService) {
    suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> = safeApiCall {
        service.getPullRequestsForUser(
            auth_token,
            "author:$author type:pr state:open",
        )
    }
}
