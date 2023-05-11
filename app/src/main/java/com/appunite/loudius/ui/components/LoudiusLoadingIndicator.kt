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

package com.appunite.loudius.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appunite.loudius.R
import com.appunite.loudius.ui.theme.LoudiusTheme
import java.util.concurrent.atomic.AtomicInteger


@Composable
fun  LoudiusLoadingIndicator(modifier: Modifier = Modifier) {
    IdlingResourceWrapper {
        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            if (LocalContext.current.areSystemAnimationsEnabled()) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_indicator))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(96.dp),
                )
            } else {
                // TODO make it centered
                LoudiusText(text = "Loading...")
            }
        }
    }
}

@SuppressLint("ObsoleteSdkInt", "Deprecated")
private fun Context.areSystemAnimationsEnabled(): Boolean {
    val duration: Float
    val transition: Float
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        duration = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f
        )
        transition = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.TRANSITION_ANIMATION_SCALE, 1.0f
        )
    } else {
        duration = Settings.System.getFloat(
            contentResolver,
            Settings.System.ANIMATOR_DURATION_SCALE, 1.0f
        )
        transition = Settings.System.getFloat(
            contentResolver,
            Settings.System.TRANSITION_ANIMATION_SCALE, 1.0f
        )
    }
    return duration != 0f && transition != 0f
}

@Preview
@Composable
fun LoudiusLoadingIndicatorPreview() {
    LoudiusTheme {
        LoudiusLoadingIndicator()
    }
}

// move to some helpers class
class CountingIdlingResource(val name: String) {
    private var counter = AtomicInteger(0)
    val isIdleNow: Boolean get() = counter.get() == 0
    fun getDiagnosticMessageIfBusy(): String = "$name is busy"

    fun increment() {
        counter.incrementAndGet()
    }
    fun decrement() {
        counter.decrementAndGet()
    }
}


// those two move to IdlingResourceWrapper.kt file:
val countingResource = CountingIdlingResource("IdlingResourceWrapper");

@Composable
fun IdlingResourceWrapper(content: @Composable () -> Unit) {
    DisposableEffect(Unit) {
        countingResource.increment()
        onDispose {
            countingResource.decrement()
        }
    }
    content()
}