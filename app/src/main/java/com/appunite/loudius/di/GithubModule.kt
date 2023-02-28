package com.appunite.loudius.di

import android.content.Context
import com.appunite.loudius.domain.AccessTokenLocalDataSource
import com.appunite.loudius.domain.UserRepository
import com.appunite.loudius.domain.UserRepositoryImpl
import com.appunite.loudius.network.GithubApi
import com.appunite.loudius.network.GithubDataSource
import com.appunite.loudius.network.GithubNetworkDataSource
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
        githubDataSource: GithubDataSource,
        accessTokenLocalDataSource: AccessTokenLocalDataSource,
    ): UserRepository = UserRepositoryImpl(githubDataSource, accessTokenLocalDataSource)

    @Singleton
    @Provides
    fun provideGithubDataSource(
        api: GithubApi,
    ): GithubDataSource = GithubNetworkDataSource(api)

    @Singleton
    @Provides
    fun provideAccessTokenLocalDataSource(@ApplicationContext context: Context): AccessTokenLocalDataSource =
        AccessTokenLocalDataSource(context)
}
