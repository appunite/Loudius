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
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import javax.inject.Singleton

val repositoryModule = module {
    singleOf(::PullRequestRepositoryImpl) { bind<PullRequestRepository>() }
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
}