package com.appunite.loudius.network.services

import com.appunite.loudius.network.model.AccessToken
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.Reviewer
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface GithubApi {

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
    ): AccessToken

    @GET("https://api.github.com/repos/{owner}/{repo}/pulls/{pull_number}/requested_reviewers")
    suspend fun getReviewers(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullRequestNumber: String,
        @Header("Authorization") token: String,
    ): List<Reviewer>

    @GET("https://api.github.com/repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    suspend fun getReviews(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullRequestNumber: String,
        @Header("Authorization") token: String,
    ): List<Review>
}
