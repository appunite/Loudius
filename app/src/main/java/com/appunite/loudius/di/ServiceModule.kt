package com.appunite.loudius.di

import com.appunite.loudius.network.services.AuthService
import com.appunite.loudius.network.services.PullRequestsService
import com.appunite.loudius.network.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {

    @Singleton
    @Provides
    fun provideAuthService(@AuthAPI retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideUserService(@BaseAPI retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Singleton
    @Provides
    fun provideReposService(@BaseAPI retrofit: Retrofit): PullRequestsService =
        retrofit.create(PullRequestsService::class.java)
}
