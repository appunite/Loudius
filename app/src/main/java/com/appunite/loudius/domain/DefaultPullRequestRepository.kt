package com.appunite.loudius.domain

import com.appunite.loudius.network.PullRequestDataSource
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.Reviewer
import javax.inject.Inject
import javax.inject.Singleton

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
}

@Singleton
class DefaultPullRequestRepository @Inject constructor(
    private val pullRequestDataSource: PullRequestDataSource
) : PullRequestRepository {
    override suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String
    ): Result<List<Review>> =
        pullRequestDataSource.getReviews(owner, repo, pullRequestNumber, "TODO ACCESS TOKEN")

    override suspend fun getReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String
    ): Result<List<Reviewer>> =
        pullRequestDataSource.getReviewers(owner, repo, pullRequestNumber, "TODO ACCESS TOKEN")
}
