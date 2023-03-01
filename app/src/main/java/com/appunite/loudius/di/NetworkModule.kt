package com.appunite.loudius.di

import com.appunite.loudius.common.Constants
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    @GitHubNonApi
    fun provideRetrofit(gson: Gson, baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    @Provides
    @Singleton
    fun provideApiRetrofit(gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    fun provideBaseUrl() = Constants.BASE_URL

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class GitHubNonApi
}
