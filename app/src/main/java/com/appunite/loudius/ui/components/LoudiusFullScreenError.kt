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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appunite.loudius.R
import com.appunite.loudius.ui.theme.LoudiusTheme

@Composable
fun LoudiusFullScreenError(
    errorText: String = stringResource(id = R.string.error_dialog_text),
    buttonText: String = stringResource(id = R.string.try_again),
    onButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(top = 142.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(56.dp),
    ) {
        ErrorImage()
        ErrorText(text = errorText)
        LoudiusOutlinedButton(
            onClick = onButtonClick,
            text = buttonText,
        )
    }
}

@Composable
private fun ErrorImage() {
    Image(
        painter = painterResource(id = R.drawable.error_image),
        contentDescription = stringResource(R.string.error_image_desc),
    )
}

@Composable
private fun ErrorText(text: String) {
    LoudiusText(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = LoudiusTextStyle.ScreenContent,
    )
}

@Preview(showSystemUi = true)
@Composable
fun LoudiusErrorScreenPreview() {
    LoudiusTheme {
        LoudiusFullScreenError(
            errorText = stringResource(id = R.string.error_dialog_text),
            buttonText = stringResource(R.string.try_again),
            onButtonClick = {},
        )
    }
}
