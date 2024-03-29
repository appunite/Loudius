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

package com.appunite.loudius

import android.app.Application
import com.appunite.loudius.di.analyticsModule
import com.appunite.loudius.di.dataSourceModule
import com.appunite.loudius.di.dispatcherModule
import com.appunite.loudius.di.githubHelperModule
import com.appunite.loudius.di.networkModule
import com.appunite.loudius.di.repositoryModule
import com.appunite.loudius.di.serviceModule
import com.appunite.loudius.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module

val appModule = module {
    includes(
        networkModule,
        dataSourceModule,
        serviceModule,
        repositoryModule,
        githubHelperModule,
        dispatcherModule,
        analyticsModule
    )
}

class LoudiusApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LoudiusApplication)
            modules(
                appModule,
                viewModelModule
            )
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}
