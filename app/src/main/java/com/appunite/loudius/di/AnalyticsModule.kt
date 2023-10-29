package com.appunite.loudius.di

import com.appunite.loudius.analytics.AnalyticsService
import com.appunite.loudius.analytics.AnalyticsServiceImpl
import com.appunite.loudius.analytics.ReviewersEventTracker
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule = module {
    single<AnalyticsService> {
        AnalyticsServiceImpl(get())
    }
    single<FirebaseAnalytics> {
        FirebaseAnalytics.getInstance(androidContext())
    }
    single<ReviewersEventTracker> {
        ReviewersEventTracker(get())
    }
}
