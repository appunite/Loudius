package com.appunite.loudius.network.datasource

import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.services.UserService
import com.appunite.loudius.network.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

interface UserDataSource {
    suspend fun getUser(): Result<User>
}

@Singleton
class UserDataSourceImpl @Inject constructor(private val userService: UserService) :
    UserDataSource {
    override suspend fun getUser(): Result<User> = safeApiCall {
        userService.getUser()
    }
}
