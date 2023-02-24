package com.appunite.loudius.network

import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.Reviewer
import com.appunite.loudius.network.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

interface PullRequestDataSource {
    suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        accessToken: String
    ): Result<List<Reviewer>>

    suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        accessToken: String
    ): Result<List<Review>>
}

@Singleton
class PullRequestNetworkDataSource @Inject constructor(
    private val api: GithubApi,
) : PullRequestDataSource {

    override suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        accessToken: String
    ): Result<List<Reviewer>> = safeApiCall {
        api.getReviewers(owner, repository, pullRequestNumber, accessToken)
    }

    override suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        accessToken: String
    ): Result<List<Review>> = safeApiCall {
        api.getReviews(owner, repository, pullRequestNumber, accessToken)
    }
}
