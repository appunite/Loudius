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

package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.request.NotifyRequestBody
import com.appunite.loudius.network.services.PullRequestsService

interface PullRequestDataSource {
    suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String
    ): Result<RequestedReviewersResponse>

    suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String
    ): Result<List<Review>>

    suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse>

    suspend fun notify(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        message: String
    ): Result<Unit>
}

class PullRequestsDataSourceImpl(
    private val service: PullRequestsService
) : PullRequestDataSource {

    override suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> =
        service.getPullRequestsForUser("author:$author type:pr state:open")

    override suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String
    ): Result<RequestedReviewersResponse> =
        service.getReviewers(owner, repository, pullRequestNumber)

    override suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String
    ): Result<List<Review>> = service.getReviews(owner, repository, pullRequestNumber)

    override suspend fun notify(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        message: String
    ): Result<Unit> =
        service.notify(owner, repository, pullRequestNumber, NotifyRequestBody(message))
}
