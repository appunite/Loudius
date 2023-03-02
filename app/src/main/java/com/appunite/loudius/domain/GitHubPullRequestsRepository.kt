package com.appunite.loudius.domain

import com.appunite.loudius.network.datasource.PullRequestsNetworkDataSource
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.Reviewer
import javax.inject.Inject

interface PullRequestRepository {
    suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String
    ): Result<List<Review>>

    suspend fun getReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String
    ): Result<List<Reviewer>>

    suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse>
}

class GitHubPullRequestsRepository @Inject constructor(private val remoteDataSource: PullRequestsNetworkDataSource) :
    PullRequestRepository {
    override suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> =
        remoteDataSource.getPullRequestsForUser(author)

    override suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String
    ): Result<List<Review>> =
        remoteDataSource.getReviews(owner, repo, pullRequestNumber, "TODO ACCESS TOKEN")

    override suspend fun getReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String
    ): Result<List<Reviewer>> =
        remoteDataSource.getReviewers(owner, repo, pullRequestNumber, "TODO ACCESS TOKEN")
}
