package com.appunite.loudius.network

import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.network.model.AccessToken
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GithubApi {

    @GET("login/oauth/authorize")
    suspend fun authorize(
        @Query("client_id") clientId: String = CLIENT_ID
    ): String

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): AccessToken
}
