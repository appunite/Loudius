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

package com.appunite.loudius.components.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appunite.loudius.components.R
import com.appunite.loudius.components.theme.LoudiusTheme
import com.loudious.components.R

@Composable
fun LoudiusLoadingIndicator(modifier: Modifier = Modifier) {
    IdlingResourceWrapper {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_indicator))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
        val loadingContentDescription = stringResource(R.string.components_loading_indicator_content_description)
        Box(
            modifier = modifier
                .fillMaxSize()
                .semantics(mergeDescendants = true) {
                    stateDescription = loadingContentDescription
                },
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
}

@Preview()
@Composable
private fun LoudiusLoadingIndicatorPreview() {
    LoudiusTheme {
        LoudiusLoadingIndicator()
    }
}
