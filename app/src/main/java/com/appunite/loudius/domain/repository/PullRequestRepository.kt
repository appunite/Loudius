/*
 * Copyright 2023 AppUnite S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appunite.loudius.domain.repository

import com.appunite.loudius.common.flatMap
import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.datasource.UserDataSource
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

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

    suspend fun notify(
        owner: String,
        repo: String,
        pullRequestNumber: String,
        message: String,
    ): Result<Unit>
}

class PullRequestRepositoryImpl @Inject constructor(
    private val pullRequestsDataSource: PullRequestDataSource,
    private val userDataSource: UserDataSource,
) : PullRequestRepository {
    override suspend fun getCurrentUserPullRequests(): Result<PullRequestsResponse> {
        val currentUser = userDataSource.getUser()
        return currentUser.flatMap { pullRequestsDataSource.getPullRequestsForUser(it.login) }
    }

    override suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>> = coroutineScope {
        val currentUserDeferred = async { userDataSource.getUser() }
        val reviewsDeferred = async {
            pullRequestsDataSource.getReviews(owner, repo, pullRequestNumber)
        }
        val currentUser = currentUserDeferred.await()
        val reviews = reviewsDeferred.await()

        currentUser.flatMap { user ->
            reviews.map { it.excludeUserReviews(user) }
        }
    }

    private fun List<Review>.excludeUserReviews(
        user: User,
    ) = filter { review -> review.user.id != user.id }

    override suspend fun getRequestedReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse> =
        pullRequestsDataSource.getReviewers(owner, repo, pullRequestNumber)

    override suspend fun notify(
        owner: String,
        repo: String,
        pullRequestNumber: String,
        message: String,
    ): Result<Unit> =
        pullRequestsDataSource.notify(owner, repo, pullRequestNumber, message)
}
