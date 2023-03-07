package com.appunite.loudius.di

import android.content.Context
import com.appunite.loudius.domain.UserLocalDataSource
import com.appunite.loudius.domain.AuthRepository
import com.appunite.loudius.domain.AuthRepositoryImpl
import com.appunite.loudius.network.datasource.AuthDataSource
import com.appunite.loudius.network.datasource.AuthNetworkDataSource
import com.appunite.loudius.network.services.GithubAuthService
import com.appunite.loudius.network.services.GithubPullRequestsService
import com.appunite.loudius.network.services.GithubUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object GithubModule {

    @Singleton
    @Provides
    fun provideGithubAuthService(@AuthAPI retrofit: Retrofit): GithubAuthService =
        retrofit.create(GithubAuthService::class.java)

    @Singleton
    @Provides
    fun provideGithubUserService(@BaseAPI retrofit: Retrofit): GithubUserService =
        retrofit.create(GithubUserService::class.java)

    @Singleton
    @Provides
    fun provideGithubReposService(@BaseAPI retrofit: Retrofit): GithubPullRequestsService =
        retrofit.create(GithubPullRequestsService::class.java)

    @Singleton
    @Provides
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        userLocalDataSource: UserLocalDataSource,
    ): AuthRepository = AuthRepositoryImpl(authDataSource, userLocalDataSource)

    @Singleton
    @Provides
    fun provideAuthServiceDataSource(
        service: GithubAuthService,
    ): AuthDataSource = AuthNetworkDataSource(service)

    @Singleton
    @Provides
    fun provideUserLocalDataSource(@ApplicationContext context: Context): UserLocalDataSource =
        UserLocalDataSource(context)
}
