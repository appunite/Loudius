/*
 * Copyright 2023 AppUnite S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appunite.loudius.di

import android.content.Context
import com.appunite.loudius.domain.store.UserLocalDataSource
import com.appunite.loudius.domain.store.UserLocalDataSourceImpl
import com.appunite.loudius.network.datasource.AuthDataSource
import com.appunite.loudius.network.datasource.AuthDataSourceImpl
import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.datasource.PullRequestsDataSourceImpl
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
    fun providePullRequestNetworkDataSource(
        service: PullRequestsService
    ): PullRequestDataSource = PullRequestsDataSourceImpl(service)

    @Provides
    @Singleton
    fun provideUserDataSource(
        userService: UserService
    ): UserDataSource = UserDataSourceImpl(userService)

    @Singleton
    @Provides
    fun provideUserLocalDataSource(@ApplicationContext context: Context): UserLocalDataSource =
        UserLocalDataSourceImpl(context)

    @Singleton
    @Provides
    fun provideAuthDataSource(
        service: AuthService
    ): AuthDataSource = AuthDataSourceImpl(service)
}
