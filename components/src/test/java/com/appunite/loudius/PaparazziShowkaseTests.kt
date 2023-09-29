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
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Density
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.airbnb.android.showkase.models.Showkase
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import com.appunite.loudius.components.getMetadata
import com.appunite.loudius.components.theme.LoudiusTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class ComponentPreview(
    private val showkaseBrowserComponent: ShowkaseBrowserComponent,
) {
    val content: @Composable () -> Unit = showkaseBrowserComponent.component
    override fun toString(): String =
        showkaseBrowserComponent.group + "-" + showkaseBrowserComponent.componentName
}

@RunWith(TestParameterInjector::class)
class PaparazziShowkaseTests {

    object PreviewProvider : TestParameter.TestParameterValuesProvider {
        override fun provideValues(): List<ComponentPreview> =
            Showkase.getMetadata().componentList.map(::ComponentPreview)
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(softButtons = false),
    )

    @Test
    fun preview_tests(
        @TestParameter(valuesProvider = PreviewProvider::class) componentPreview: ComponentPreview,
        @TestParameter(value = ["1.0", "2"]) fontScale: Float,
        @TestParameter(value = ["light"]) theme: String,
    ) {
        paparazzi.snapshot {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = fontScale,
                ),
            ) {
                LoudiusTheme(darkTheme = (theme == "dark")) {
                    Box(modifier = Modifier.background(Color.White)) {
                        componentPreview.content()
                    }
                }
            }
        }
    }
}
