package com.appunite.loudius

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import com.appunite.loudius.di.dataSourceModule
import com.appunite.loudius.di.dispatcherModule
import com.appunite.loudius.di.githubHelperModule
import com.appunite.loudius.di.networkModule
import com.appunite.loudius.di.repositoryModule
import com.appunite.loudius.di.serviceModule
import com.appunite.loudius.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                dataSourceModule,
                dispatcherModule,
                githubHelperModule,
                networkModule,
                repositoryModule,
                serviceModule,
                viewModelModule
            )

            androidContext(this@TestApplication)
        }
    }
}