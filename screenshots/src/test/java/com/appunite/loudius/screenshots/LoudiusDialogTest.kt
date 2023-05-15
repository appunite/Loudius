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

package com.appunite.loudius.screenshots

import androidx.compose.ui.res.stringResource
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.appunite.loudius.screenshots.components.LoudiusDialog
import com.appunite.loudius.screenshots.components.LoudiusErrorDialog
import com.appunite.loudius.screenshots.components.LoudiusFullScreenError
import com.appunite.loudius.screenshots.components.LoudiusOutlinedButton
import com.appunite.loudius.screenshots.components.LoudiusText
import com.appunite.loudius.screenshots.components.LoudiusTextStyle
import com.appunite.loudius.screenshots.theme.LoudiusTheme
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
            LoudiusTheme {
                LoudiusDialog(
                    onDismissRequest = { },
                    title = "Title",
                    text = {
                        LoudiusText(
                            style = LoudiusTextStyle.ScreenContent,
                            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse dapibus elit justo, at pharetra nulla mattis vel. Integer gravida tortor sed fringilla viverra. Duis scelerisque ante neque, a pretium eros.",
                        )
                    },
                    confirmButton = {
                        LoudiusOutlinedButton(text = "Confirm") {}
                    },
                    dismissButton = {
                        LoudiusOutlinedButton(text = "Dismiss") {}
                    },
                )
            }
        }
    }

    @Test
    fun loudiusErrorDialogTest() {
        paparazzi.snapshot {
            LoudiusTheme {
                LoudiusErrorDialog(onConfirmButtonClick = {})
            }
        }
    }

    @Test
    fun loudiusFullScreenErrorTest() {
        paparazzi.snapshot {
            LoudiusTheme(darkTheme = false) {
                LoudiusFullScreenError(
                    errorText = stringResource(id = R.string.error_dialog_text),
                    buttonText = stringResource(R.string.try_again),
                    onButtonClick = {},
                )
            }
        }
    }
}
