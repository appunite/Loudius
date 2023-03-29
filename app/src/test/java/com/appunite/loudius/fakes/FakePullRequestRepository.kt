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

    private val initialReviewsAnswer = Result.success(Defaults.reviews())
    private val initialRequestedReviewersAnswer = Result.success(
        RequestedReviewersResponse(Defaults.requestedReviewers()),
    )
    private val initialPullRequestAnswer = Result.success(
        PullRequestsResponse(
            incompleteResults = false,
            totalCount = 1,
            items = listOf(
                Defaults.pullRequest(),
            ),
        ),
    )

    private val initialNotifyAnswer: suspend (pullRequestNumber: String) -> Result<Unit> =
        { pullRequestNumber: String ->
            when (pullRequestNumber) {
                "correctPullRequestNumber" -> Result.success(Unit)
                "notExistingPullRequestNumber" -> Result.failure(
                    WebException.UnknownError(404, null),
                )
                else -> Result.failure(WebException.NetworkError())
            }
        }

    private var lazyReviewsAnswer: suspend () -> Result<List<Review>> = { initialReviewsAnswer }
    private var lazyRequestedReviewersAnswer: suspend () -> Result<RequestedReviewersResponse> =
        { initialRequestedReviewersAnswer }
    private var lazyCurrentUserPullRequests: suspend () -> Result<PullRequestsResponse> =
        { initialPullRequestAnswer }
    private var lazyNotifyAnswer: suspend (pullRequestNumber: String) -> Result<Unit> =
        initialNotifyAnswer

    override suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>> = lazyReviewsAnswer()

    fun setReviewsAnswer(result: suspend () -> Result<List<Review>>) {
        lazyReviewsAnswer = result
    }

    fun resetReviewsAnswer() {
        lazyReviewsAnswer = { initialReviewsAnswer }
    }

    override suspend fun getRequestedReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse> = lazyRequestedReviewersAnswer()

    fun setRequestedReviewersAnswer(result: suspend () -> Result<RequestedReviewersResponse>) {
        lazyRequestedReviewersAnswer = result
    }

    fun resetRequestedReviewersAnswer() {
        lazyRequestedReviewersAnswer = { initialRequestedReviewersAnswer }
    }

    fun setCurrentUserPullRequests(result: suspend () -> Result<PullRequestsResponse>) {
        lazyCurrentUserPullRequests = result
    }

    fun resetCurrentUserPullRequestAnswer() {
        lazyCurrentUserPullRequests = { initialPullRequestAnswer }
    }

    override suspend fun getCurrentUserPullRequests(): Result<PullRequestsResponse> {
        return lazyCurrentUserPullRequests()
    }

    fun setNotifyResponse(result: suspend (pullRequestNumber: String) -> Result<Unit>) {
        lazyNotifyAnswer = result
    }

    fun resetNotifyResponse() {
        lazyNotifyAnswer = initialNotifyAnswer
    }

    override suspend fun notify(
        owner: String,
        repo: String,
        pullRequestNumber: String,
        message: String,
    ): Result<Unit> = lazyNotifyAnswer(pullRequestNumber)
}
