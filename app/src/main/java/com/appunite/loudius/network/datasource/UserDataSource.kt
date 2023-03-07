package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.services.GithubUserService
import com.appunite.loudius.network.utils.safeApiCall
import javax.inject.Inject

class UserDataSource @Inject constructor(private val userService: GithubUserService) {
    suspend fun getUser(): Result<User> = safeApiCall {
        userService.getUser()
    }
}
