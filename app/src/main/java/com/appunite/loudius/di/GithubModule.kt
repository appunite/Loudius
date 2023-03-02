package com.appunite.loudius.di

import com.appunite.loudius.domain.UserRepository
import com.appunite.loudius.domain.UserRepositoryImpl
import com.appunite.loudius.network.GithubDataSource
import com.appunite.loudius.network.GithubNetworkDataSource
import com.appunite.loudius.network.services.GithubApi
import com.appunite.loudius.network.services.GithubPullRequestsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object GithubModule {

    @Singleton
    @Provides
    fun provideGithubApi(@AuthAPI retrofit: Retrofit): GithubApi =
        retrofit.create(GithubApi::class.java)

    @Singleton
    @Provides
    fun provideUserRepository(
        githubDataSource: GithubDataSource,
    ): UserRepository = UserRepositoryImpl(githubDataSource)

    @Singleton
    @Provides
    fun provideGithubDataSource(
        api: GithubApi,
    ): GithubDataSource = GithubNetworkDataSource(api)

    @Provides
    fun provideGithubReposService(@BaseAPI retrofit: Retrofit): GithubPullRequestsService =
        retrofit.create(GithubPullRequestsService::class.java)
}
