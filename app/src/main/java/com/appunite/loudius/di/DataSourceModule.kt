package com.appunite.loudius.di

import android.content.Context
import com.appunite.loudius.domain.store.UserLocalDataSource
import com.appunite.loudius.network.datasource.AuthDataSource
import com.appunite.loudius.network.datasource.AuthNetworkDataSource
import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.datasource.PullRequestsNetworkDataSource
import com.appunite.loudius.network.datasource.UserDataSource
import com.appunite.loudius.network.datasource.UserDataSourceImpl
import com.appunite.loudius.network.services.AuthService
import com.appunite.loudius.network.services.PullRequestsService
import com.appunite.loudius.network.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataSourceModule {

    @Provides
    @Singleton
    fun providePullRequestNetworkDataSource(service: PullRequestsService): PullRequestDataSource =
        PullRequestsNetworkDataSource(service)

    @Provides
    @Singleton
    fun provideUserDataSource(userService: UserService): UserDataSource =
        UserDataSourceImpl(userService)

    @Singleton
    @Provides
    fun provideUserLocalDataSource(@ApplicationContext context: Context): UserLocalDataSource =
        UserLocalDataSource(context)

    @Singleton
    @Provides
    fun provideAuthNetworkDataSource(
        service: AuthService,
    ): AuthDataSource = AuthNetworkDataSource(service)
}
