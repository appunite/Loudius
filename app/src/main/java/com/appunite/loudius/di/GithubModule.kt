package com.appunite.loudius.di

import android.content.Context
import com.appunite.loudius.domain.UserLocalDataSource
import com.appunite.loudius.domain.UserRepository
import com.appunite.loudius.domain.UserRepositoryImpl
import com.appunite.loudius.network.GithubApi
import com.appunite.loudius.network.UserDataSource
import com.appunite.loudius.network.UserNetworkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
object GithubModule {

    @Singleton
    @Provides
    fun provideGithubApi(retrofit: Retrofit): GithubApi = retrofit.create(GithubApi::class.java)

    @Singleton
    @Provides
    fun provideUserRepository(
        userDataSource: UserDataSource,
        userLocalDataSource: UserLocalDataSource,
    ): UserRepository = UserRepositoryImpl(userDataSource, userLocalDataSource)

    @Singleton
    @Provides
    fun provideGithubDataSource(
        api: GithubApi,
    ): UserDataSource = UserNetworkDataSource(api)

    @Singleton
    @Provides
    fun provideAccessTokenLocalDataSource(@ApplicationContext context: Context): UserLocalDataSource =
        UserLocalDataSource(context)
}
