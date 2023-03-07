package com.appunite.loudius.di

import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.datasource.PullRequestsNetworkDataSource
import com.appunite.loudius.network.services.GithubPullRequestsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PullRequestModule {

    @Provides
    @Singleton
    fun providePullRequestNetworkDataSource(service: GithubPullRequestsService): PullRequestDataSource =
        PullRequestsNetworkDataSource(service)
}
