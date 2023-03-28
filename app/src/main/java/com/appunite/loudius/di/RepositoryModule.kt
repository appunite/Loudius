package com.appunite.loudius.di

import com.appunite.loudius.domain.repository.AuthRepository
import com.appunite.loudius.domain.repository.AuthRepositoryImpl
import com.appunite.loudius.domain.repository.PullRequestRepository
import com.appunite.loudius.domain.repository.PullRequestRepositoryImpl
import com.appunite.loudius.domain.store.UserLocalDataSource
import com.appunite.loudius.network.datasource.AuthDataSource
import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.datasource.UserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun providePullRequestRepository(
        dataSource: PullRequestDataSource,
        userDataSource: UserDataSource,
    ): PullRequestRepository = PullRequestRepositoryImpl(dataSource, userDataSource)

    @Singleton
    @Provides
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        userLocalDataSource: UserLocalDataSource,
    ): AuthRepository = AuthRepositoryImpl(authDataSource, userLocalDataSource)
}
