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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PullRequestsService {
    @GET("/search/issues")
    suspend fun getPullRequestsForUser(
        @Query("q", encoded = true) query: String,
        @Query("page") page: Int = 0,
        @Query("per_page") perPage: Int = 100,
    ): PullRequestsResponse

    @GET("/repos/{owner}/{repo}/pulls/{pull_number}/requested_reviewers")
    suspend fun getReviewers(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullRequestNumber: String,
    ): RequestedReviewersResponse

    @GET("/repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    suspend fun getReviews(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullRequestNumber: String,
    ): List<Review>

    @POST("/repos/{owner}/{repo}/issues/{issue_number}/comments")
    suspend fun notify(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: String,
        @Body body: NotifyRequestBody,
    )
}
