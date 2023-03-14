package com.appunite.loudius.domain

import com.appunite.loudius.common.flatMap
import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.datasource.UserDataSource
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.User
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

interface PullRequestRepository {
    suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>>

    suspend fun getRequestedReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse>

    suspend fun getCurrentUserPullRequests(): Result<PullRequestsResponse>
}

class PullRequestRepositoryImpl @Inject constructor(
    private val pullRequestsNetworkDataSource: PullRequestDataSource,
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
    ): Result<List<Review>> = withContext(coroutineContext) {
        val currentUserDeferred = async { userDataSource.getUser() }
        val reviewsDeferred = async {
            pullRequestsNetworkDataSource.getReviews(owner, repo, pullRequestNumber)
        }
        val currentUser = currentUserDeferred.await()
        val reviews = reviewsDeferred.await()

        return@withContext currentUser.flatMap { user ->
            reviews.map { it.excludeUserReviews(user) }
        }
    }

    private fun List<Review>.excludeUserReviews(
        user: User
    ) = filter { review -> review.user.id != user.id }

    override suspend fun getRequestedReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse> =
        pullRequestsNetworkDataSource.getReviewers(owner, repo, pullRequestNumber)
}
