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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appunite.loudius.R
import com.appunite.loudius.ui.components.utils.MultiScreenPreviews
import com.appunite.loudius.ui.theme.LoudiusTheme

@Composable
fun LoudiusFullScreenError(
    modifier: Modifier = Modifier,
    errorText: String = stringResource(id = R.string.error_dialog_description),
    buttonText: String = stringResource(id = R.string.error_dialog_try_again_button),
    onButtonClick: () -> Unit,
) {
    ScreenErrorWithSpacers(
        modifier = modifier,
        errorText = errorText,
        buttonText = buttonText,
        onButtonClick = onButtonClick,
    )
}

@Composable
fun ScreenErrorWithSpacers(
    modifier: Modifier,
    errorText: String,
    buttonText: String,
    onButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(32.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(weight = 0.15f))
        ErrorImage(
            modifier = Modifier
                .weight(weight = .35f)
                .sizeIn(maxWidth = 400.dp, maxHeight = 400.dp)
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.weight(weight = 0.05f))
        ErrorText(text = errorText)
        LoudiusOutlinedButton(
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = onButtonClick,
            text = buttonText,
        )
        Spacer(modifier = Modifier.weight(weight = 0.25f))
    }
}

@Composable
private fun ErrorImage(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.error_image),
        contentDescription = stringResource(R.string.error_dialog_image_content_description),
    )
}

@Composable
private fun ErrorText(text: String) {
    LoudiusText(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        style = LoudiusTextStyle.ScreenContent,
    )
}

@MultiScreenPreviews
@Composable
fun LoudiusErrorScreenPreview() {
    LoudiusTheme {
        LoudiusFullScreenError {}
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoudiusErrorScreenCustomTextsPreview() {
    LoudiusTheme {
        LoudiusFullScreenError(
            errorText = "Custom title",
            buttonText = "My Button Text",
        ) {}
    }
}
