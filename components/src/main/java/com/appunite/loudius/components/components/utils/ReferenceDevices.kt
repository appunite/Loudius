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

package com.appunite.loudius.components.components.utils

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

object ReferenceDevices {
    const val SmallPhone =
        "spec:id=reference_phone,shape=Normal,width=350,height=600,unit=dp,dpi=420"
    const val SmallPhoneLandscape =
        "spec:id=reference_phone,shape=Normal,width=600,height=350,unit=dp,dpi=420"
    const val Default = Devices.DEFAULT
    const val Tablet = Devices.TABLET
    const val TabletPortrait = "spec:shape=Normal,width=800,height=1280,unit=dp,dpi=420"
}

@Preview(
    showSystemUi = true,
    name = "small phone - portrait",
    group = "multi screen",
    device = ReferenceDevices.SmallPhone
)
@Preview(
    showSystemUi = true,
    name = "small phone - landscape",
    group = "multi screen",
    device = ReferenceDevices.SmallPhoneLandscape
)
@Preview(
    showSystemUi = true,
    name = "default phone",
    group = "multi screen",
    device = ReferenceDevices.Default
)
@Preview(
    showSystemUi = true,
    name = "tablet - landscape",
    group = "multi screen",
    device = ReferenceDevices.Tablet
)
@Preview(
    showSystemUi = true,
    name = "tablet - portrait",
    group = "multi screen",
    device = ReferenceDevices.TabletPortrait
)
annotation class MultiScreenPreviews
