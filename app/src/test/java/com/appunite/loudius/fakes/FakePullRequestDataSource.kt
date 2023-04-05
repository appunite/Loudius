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

import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewer
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState
import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.utils.WebException
import com.appunite.loudius.util.Defaults
import java.time.LocalDateTime

class FakePullRequestDataSource : PullRequestDataSource {

    private val date1 = LocalDateTime.parse("2022-01-29T10:00:00")

    override suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String,
    ): Result<List<Review>> = when (pullRequestNumber) {
        "correctPullRequestNumber", "onlyReviewsPullNumber" -> Result.success(
            Defaults.reviews()
        )
        else -> Result.failure(WebException.UnknownError(404, null))
    }

    override suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse> = when (pullRequestNumber) {
        "correctPullRequestNumber", "onlyRequestedReviewersPullNumber" -> Result.success(
            RequestedReviewersResponse(
                listOf(
                    RequestedReviewer(3, "user3"),
                    RequestedReviewer(4, "user4"),
                ),
            ),
        )
        else -> Result.failure(WebException.UnknownError(404, null))
    }

    override suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> = when (author) {
        "correctAuthor" -> Result.success(Defaults.pullRequestsResponse())
        else -> Result.failure(WebException.UnknownError(422, null))
    }

    override suspend fun notify(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        message: String,
    ): Result<Unit> = when (pullRequestNumber) {
        "correctPullRequestNumber" -> Result.success(Unit)
        else -> Result.failure(WebException.UnknownError(404, null))
    }
}
