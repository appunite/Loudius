package com.appunite.loudius.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appunite.loudius.R
import com.appunite.loudius.ui.theme.LoudiusTheme

@Composable
fun LoudiusLoadingIndicator() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_indicator))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .align(Alignment.Center)
                .size(96.dp),
        )
    }
}

@Preview
@Composable
fun LoudiusLoadingIndicatorPreview() {
    LoudiusTheme {
        LoudiusLoadingIndicator()
    }
}
