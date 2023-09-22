package com.appunite.loudius.di


import android.content.Context
import io.mockk.mockk
import io.mockk.mockkClass
import org.junit.jupiter.api.Test
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProvider

class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {
        MockProvider.register{mockkClass(it)}

        koinApplication {
            modules(
                dataSourceModule,
                dispatcherModule,
                githubHelperModule,
                networkModule,
                repositoryModule,
                serviceModule
            )
            checkModules() {
                withInstance<Context>()
            }
        }
    }
}