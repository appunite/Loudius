package com.appunite.loudius.domain

import com.appunite.loudius.common.flatMap
import com.appunite.loudius.network.datasource.PullRequestsNetworkDataSource
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

class GitHubPullRequestsRepository @Inject constructor(private val remoteDataSource: PullRequestsNetworkDataSource) :
    PullRequestRepository {
    override suspend fun getCurrentUserPullRequests(): Result<PullRequestsResponse> {
        val currentUser = remoteDataSource.getUser()
        return currentUser.flatMap { remoteDataSource.getPullRequestsForUser(it.login) }
    }

    override suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>> =
        remoteDataSource.getReviews(owner, repo, pullRequestNumber)

    override suspend fun getReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse> =
        remoteDataSource.getReviewers(owner, repo, pullRequestNumber)
}
