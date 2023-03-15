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
        @Body body: NotifyRequestBody
    )

}
