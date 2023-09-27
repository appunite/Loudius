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

package com.appunite.loudius.network.services

import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.request.NotifyRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLParameter

interface PullRequestsService {

    suspend fun getPullRequestsForUser(
        query: String,
        page: Int = 0,
        perPage: Int = 100,
    ): Result<PullRequestsResponse>

    suspend fun getReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse>

    suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>>

    suspend fun notify(
        owner: String,
        repo: String,
        issueNumber: String,
        body: NotifyRequestBody,
    ): Result<Unit>
}

class PullRequestsServiceImpl(
    private val client: HttpClient
) :  PullRequestsService {
    override suspend fun getPullRequestsForUser(
        query: String,
        page: Int,
        perPage: Int,
    ): Result<PullRequestsResponse> = runCatching {
        client.get("/search/issues") {
            parameter("q".encodeURLParameter(), query)
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    override suspend fun getReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<RequestedReviewersResponse> = runCatching {
        client.get("/repos/$owner/$repo/pulls/$pullRequestNumber/requested_reviewers").body()
    }

    override suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>> = runCatching {
        client.get("/repos/$owner/$repo/pulls/$pullRequestNumber/reviews").body()
    }

    override suspend fun notify(
        owner: String,
        repo: String,
        issueNumber: String,
        body: NotifyRequestBody,
    ): Result<Unit> = runCatching {
        client.post("/repos/$owner/$repo/issues/$issueNumber/comments") {
            setBody(body)
            contentType(ContentType.Application.Json)
        }.body()
    }
}
