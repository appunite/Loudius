package com.appunite.loudius.di

import com.appunite.loudius.domain.PullRequestRepository
import com.appunite.loudius.domain.PullRequestRepositoryImpl
import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.datasource.PullRequestsNetworkDataSource
import com.appunite.loudius.network.datasource.UserDataSource
import com.appunite.loudius.network.services.PullRequestsService
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
    fun providePullRequestNetworkDataSource(service: PullRequestsService): PullRequestDataSource =
        PullRequestsNetworkDataSource(service)

    @Provides
    @Singleton
    fun providePullRequestRepository(
        dataSource: PullRequestDataSource,
        userDataSource: UserDataSource,
    ): PullRequestRepository = PullRequestRepositoryImpl(dataSource, userDataSource)
}
