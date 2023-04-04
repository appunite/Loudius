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

package com.appunite.loudius.fakes

import com.appunite.loudius.domain.repository.PullRequestRepository
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.Defaults

class FakePullRequestRepository : PullRequestRepository {
    override suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>> = Result.success(Defaults.reviews())

    override suspend fun getRequestedReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse> =
        Result.success(RequestedReviewersResponse(Defaults.requestedReviewers()))

    override suspend fun getCurrentUserPullRequests(): Result<PullRequestsResponse> =
        Result.success(Defaults.pullRequestsResponse())

    override suspend fun notify(
        owner: String,
        repo: String,
        pullRequestNumber: String,
        message: String,
    ): Result<Unit> = when (pullRequestNumber) {
        "correctPullRequestNumber" -> Result.success(Unit)
        else -> Result.failure(WebException.UnknownError(404, null))
    }
}
