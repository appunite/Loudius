package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.services.GithubPullRequestsService
import com.appunite.loudius.network.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

interface PullRequestDataSource {
    suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        accessToken: String,
    ): Result<RequestedReviewersResponse>

    suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        accessToken: String,
    ): Result<List<Review>>
}

const val auth_token = "BEARER xxxxxxx" // temporary solution

@Singleton
class PullRequestsNetworkDataSource @Inject constructor(private val service: GithubPullRequestsService) :
    PullRequestDataSource {
    suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> = safeApiCall {
        service.getPullRequestsForUser(auth_token, "author:$author type:pr state:open")
    }

    override suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        accessToken: String,
    ): Result<RequestedReviewersResponse> = safeApiCall {
        service.getReviewers(owner, repository, pullRequestNumber, accessToken)
    }

    override suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        accessToken: String,
    ): Result<List<Review>> = safeApiCall {
        service.getReviews(owner, repository, pullRequestNumber, accessToken)
    }
}
