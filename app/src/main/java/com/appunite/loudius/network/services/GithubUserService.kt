package com.appunite.loudius.network.services

import com.appunite.loudius.network.model.User
import retrofit2.http.GET
import retrofit2.http.Headers

interface GithubUserService {
    @Headers("Accept: application/json")
    @GET("user")
    suspend fun getUser(): User
}
