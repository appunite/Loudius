package com.appunite.loudius.network

import com.appunite.loudius.network.model.PullRequestsResponse
import javax.inject.Inject

class GitHubPullRequestsRepository @Inject constructor(private val remoteDataSource: GitHubPullRequestsDataSource) {
    suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> =
        remoteDataSource.getPullRequestsForUser(author)
}
