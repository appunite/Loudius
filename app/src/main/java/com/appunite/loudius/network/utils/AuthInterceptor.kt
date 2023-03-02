package com.appunite.loudius.network.utils

import com.appunite.loudius.domain.UserRepository
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val userRepository: UserRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authenticatedRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${userRepository.getAccessToken()}")
            .build()
        return chain.proceed(authenticatedRequest)
    }
}
