package com.appunite.loudius.di

import android.content.Context
import com.appunite.loudius.analytics.AnalyticsService
import com.appunite.loudius.analytics.AnalyticsServiceImpl
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsService(
        analytics: FirebaseAnalytics
    ): AnalyticsService = AnalyticsServiceImpl(analytics)

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
}
