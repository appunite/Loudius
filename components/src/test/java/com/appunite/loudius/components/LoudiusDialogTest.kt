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

package com.appunite.loudius.components

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.appunite.loudius.components.components.LoudiusDialogAdvancedPreview
import com.appunite.loudius.components.components.LoudiusErrorDialogPreview
import com.appunite.loudius.components.components.LoudiusErrorScreenPreview
import org.junit.Rule
import org.junit.Test

class LoudiusDialogTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        renderingMode = SessionParams.RenderingMode.V_SCROLL,
        showSystemUi = false,
    )

    @Test
    fun loudiusDialogTest() {
        paparazzi.snapshot {
            LoudiusDialogAdvancedPreview()
        }
    }

    @Test
    fun loudiusErrorDialogTest() {
        paparazzi.snapshot {
            LoudiusErrorDialogPreview()
        }
    }

    @Test
    fun loudiusFullScreenErrorTest() {
        paparazzi.snapshot {
            LoudiusErrorScreenPreview()
        }
    }
}
