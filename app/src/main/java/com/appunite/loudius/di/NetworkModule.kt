package com.appunite.loudius.di

import com.appunite.loudius.common.Constants
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @AuthAPI
    fun provideBaseAuthUrl() = Constants.BASE_AUTH_URL

    @Provides
    @BaseAPI
    fun provideBaseAPIUrl() = Constants.BASE_API_URL

    @Provides
    @Singleton
    @AuthAPI
    fun provideAuthRetrofit(gson: Gson, @AuthAPI baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    @BaseAPI
    fun provideBaseRetrofit(gson: Gson, @BaseAPI baseAPIUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseAPIUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

}
