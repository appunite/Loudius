package com.appunite.loudius.domain

import com.appunite.loudius.network.datasource.PullRequestsNetworkDataSource
import com.appunite.loudius.network.model.PullRequestsResponse
import javax.inject.Inject

class GitHubPullRequestsRepository @Inject constructor(private val remoteDataSource: PullRequestsNetworkDataSource) {
    suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> =
        remoteDataSource.getPullRequestsForUser(author)
}
