package com.appunite.loudius.network.services

import com.appunite.loudius.network.model.PullRequestsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GithubPullRequestsService {
    @GET("/search/issues")
    suspend fun getPullRequestsForUser(
        @Header("Authorization") authorization: String,
        @Query("q", encoded = true) query: String,
        @Query("page") page: Int = 0,
        @Query("per_page") perPage: Int = 100,
    ): PullRequestsResponse
}
