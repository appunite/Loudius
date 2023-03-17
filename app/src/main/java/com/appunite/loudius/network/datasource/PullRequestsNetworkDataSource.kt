package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.request.NotifyRequestBody
import com.appunite.loudius.network.services.PullRequestsService
import com.appunite.loudius.network.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

interface PullRequestDataSource {
    suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse>

    suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String,
    ): Result<List<Review>>

    suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse>

    suspend fun notify(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        message: String,
    ): Result<Unit>
}

@Singleton
class PullRequestsNetworkDataSource @Inject constructor(private val service: PullRequestsService) :
    PullRequestDataSource {
    override suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> =
        safeApiCall {
            service.getPullRequestsForUser("author:$author type:pr state:open")
        }

    override suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse> = safeApiCall {
        service.getReviewers(owner, repository, pullRequestNumber)
    }

    override suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String,
    ): Result<List<Review>> = safeApiCall {
        service.getReviews(owner, repository, pullRequestNumber)
    }

    override suspend fun notify(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        message: String,
    ): Result<Unit> = safeApiCall {
        service.notify(owner, repository, pullRequestNumber, NotifyRequestBody(message))
    }
}
