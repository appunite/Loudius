package com.appunite.loudius.util

import java.util.Locale

/**
 * Returns true if test is androidTest, returns false if this is unit test or robolectric test
 */
val isAndroidTest =
    System.getProperty("java.runtime.name")
        ?.lowercase(Locale.US)
        ?.contains("android")
        ?: false
