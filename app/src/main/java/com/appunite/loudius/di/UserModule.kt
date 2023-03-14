package com.appunite.loudius.di

import com.appunite.loudius.network.datasource.UserDataSource
import com.appunite.loudius.network.datasource.UserDataSourceImpl
import com.appunite.loudius.network.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UserModule {

    @Provides
    @Singleton
    fun provideUserDataSource(userService: UserService): UserDataSource =
        UserDataSourceImpl(userService)

}
