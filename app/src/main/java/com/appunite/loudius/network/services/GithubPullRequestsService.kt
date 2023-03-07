package com.appunite.loudius.network.services

import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubPullRequestsService {
    @GET("/search/issues")
    suspend fun getPullRequestsForUser(
        @Header("Authorization") authorization: String,
        @Query("q", encoded = true) query: String,
        @Query("page") page: Int = 0,
        @Query("per_page") perPage: Int = 100,
    ): PullRequestsResponse

    @GET("/repos/{owner}/{repo}/pulls/{pull_number}/requested_reviewers")
    suspend fun getReviewers(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullRequestNumber: String,
        @Header("Authorization") token: String,
    ): RequestedReviewersResponse

    @GET("/repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    suspend fun getReviews(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullRequestNumber: String,
        @Header("Authorization") token: String,
    ): List<Review>
}
