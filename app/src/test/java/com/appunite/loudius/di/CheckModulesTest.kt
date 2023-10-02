package com.appunite.loudius.di

import android.content.Context
import android.content.SharedPreferences
import com.appunite.loudius.appModule
import com.appunite.loudius.util.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockkClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProvider

@ExtendWith(MainDispatcherExtension::class)
class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {
        val mockContext = mockkClass(Context::class)
        val mockSharedPref = mockkClass(SharedPreferences::class)

        every { mockContext.getSharedPreferences(any(), any()) } returns mockSharedPref

        MockProvider.register { mockkClass(it) }

        koinApplication {
            modules(
               appModule
            )

            checkModules() {
                withInstance(mockContext)
            }
        }
    }
}
