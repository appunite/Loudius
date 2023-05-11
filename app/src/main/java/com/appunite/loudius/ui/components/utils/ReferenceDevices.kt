package com.appunite.loudius.ui.components.utils

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
    device = ReferenceDevices.SmallPhone,
)
@Preview(
    showSystemUi = true,
    name = "small phone - landscape",
    group = "multi screen",
    device = ReferenceDevices.SmallPhoneLandscape,
)
@Preview(
    showSystemUi = true,
    name = "default phone",
    group = "multi screen",
    device = ReferenceDevices.Default,
)
@Preview(
    showSystemUi = true,
    name = "tablet - landscape",
    group = "multi screen",
    device = ReferenceDevices.Tablet,
)
@Preview(
    showSystemUi = true,
    name = "tablet - portrait",
    group = "multi screen",
    device = ReferenceDevices.TabletPortrait,
)
annotation class MultiScreenPreviews
