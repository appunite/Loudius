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

import com.appunite.loudius.network.services.AuthService
import com.appunite.loudius.network.services.AuthServiceImpl
import com.appunite.loudius.network.services.PullRequestsService
import com.appunite.loudius.network.services.UserService
import com.appunite.loudius.network.services.UserServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import javax.inject.Singleton

val serviceModule = module {
    singleOf(::AuthServiceImpl) { bind<AuthService>() }
    singleOf(::UserServiceImpl) { bind<UserService>() }
}

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {

    @Singleton
    @Provides
    fun provideAuthService(@AuthAPI httpClient: HttpClient): AuthService =
        AuthServiceImpl(httpClient)

    @Singleton
    @Provides
    fun provideUserService(@BaseAPI httpClient: HttpClient): UserService =
        UserServiceImpl(httpClient)

    @Singleton
    @Provides
    fun provideReposService(@BaseAPI retrofit: Retrofit): PullRequestsService =
        retrofit.create(PullRequestsService::class.java)
}
