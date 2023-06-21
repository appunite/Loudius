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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.appunite.loudius.components.components.LoudiusOutlinedButtonDisabledPreview
import com.appunite.loudius.components.components.LoudiusOutlinedButtonLargePreview
import com.appunite.loudius.components.components.LoudiusOutlinedButtonWithIconLargePreview
import com.appunite.loudius.components.components.LoudiusOutlinedButtonWithIconPreview
import org.junit.Rule
import org.junit.Test

class LoudiusButtonTests {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.V_SCROLL,
        showSystemUi = false,
    )

    @Test
    fun loudiusOutlinedButton() {
        paparazzi.snapshot {
            Column(Modifier.background(Color.White)) {
                LoudiusOutlinedButtonWithIconLargePreview()
                LoudiusOutlinedButtonDisabledPreview()
                LoudiusOutlinedButtonWithIconPreview()
                LoudiusOutlinedButtonLargePreview()
            }
        }
    }
}
