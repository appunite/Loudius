package com.appunite.loudius.di

import android.content.Context
import com.appunite.loudius.domain.UserLocalDataSource
import com.appunite.loudius.domain.repository.AuthRepository
import com.appunite.loudius.domain.repository.AuthRepositoryImpl
import com.appunite.loudius.network.datasource.AuthDataSource
import com.appunite.loudius.network.datasource.AuthNetworkDataSource
import com.appunite.loudius.network.services.AuthService
import com.appunite.loudius.network.services.PullRequestsService
import com.appunite.loudius.network.services.UserService
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
    fun provideAuthService(@AuthAPI retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideUserService(@BaseAPI retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Singleton
    @Provides
    fun provideReposService(@BaseAPI retrofit: Retrofit): PullRequestsService =
        retrofit.create(PullRequestsService::class.java)

    @Singleton
    @Provides
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        userLocalDataSource: UserLocalDataSource,
    ): AuthRepository = AuthRepositoryImpl(authDataSource, userLocalDataSource)

    @Singleton
    @Provides
    fun provideAuthServiceDataSource(
        service: AuthService,
    ): AuthDataSource = AuthNetworkDataSource(service)

    @Singleton
    @Provides
    fun provideUserLocalDataSource(@ApplicationContext context: Context): UserLocalDataSource =
        UserLocalDataSource(context)
}
