package com.appunite.loudius
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.contains

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
@DisplayName("ensure robolectric tests setup is set correctly")
class RobolectricSetupTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `ensure context is mocked correctly`() {
        expectThat(context.packageName).contains("com.appunite.loudius")
    }
}