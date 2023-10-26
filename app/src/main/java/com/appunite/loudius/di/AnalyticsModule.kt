package com.appunite.loudius.di

import com.appunite.loudius.analytics.AnalyticsService
import com.appunite.loudius.analytics.AnalyticsServiceImpl
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule = module {
    single<AnalyticsService> {
        val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(androidContext())
        AnalyticsServiceImpl(firebaseAnalytics)
    }
}
