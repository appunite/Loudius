package com.appunite.loudius.domain

import com.appunite.loudius.common.flatMap
import com.appunite.loudius.network.datasource.PullRequestsNetworkDataSource
import com.appunite.loudius.network.datasource.UserDataSource
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import javax.inject.Inject

interface PullRequestRepository {
    suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>>

    suspend fun getReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse>

    suspend fun getCurrentUserPullRequests(): Result<PullRequestsResponse>
}

class PullRequestRepositoryImpl @Inject constructor(
    private val pullRequestsNetworkDataSource: PullRequestsNetworkDataSource,
    private val userDataSource: UserDataSource,
) : PullRequestRepository {
    override suspend fun getCurrentUserPullRequests(): Result<PullRequestsResponse> {
        val currentUser = userDataSource.getUser()
        return currentUser.flatMap { pullRequestsNetworkDataSource.getPullRequestsForUser(it.login) }
    }

    override suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>> =
        pullRequestsNetworkDataSource.getReviews(owner, repo, pullRequestNumber)

    override suspend fun getReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse> =
        pullRequestsNetworkDataSource.getReviewers(owner, repo, pullRequestNumber)
}
