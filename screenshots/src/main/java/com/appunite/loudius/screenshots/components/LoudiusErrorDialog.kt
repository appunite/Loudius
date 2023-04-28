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

package com.appunite.loudius.screenshots.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appunite.loudius.screenshots.R
import com.appunite.loudius.screenshots.theme.LoudiusTheme

@Composable
fun LoudiusErrorDialog(
    onConfirmButtonClick: () -> Unit,
    dialogTitle: String = stringResource(id = R.string.error_dialog_title),
    dialogText: String = stringResource(id = R.string.error_dialog_text),
    confirmText: String = stringResource(R.string.ok),
) {
    var openDialog by remember { mutableStateOf(true) }
    if (openDialog) {
        LoudiusDialog(
            onDismissRequest = { openDialog = false },
            title = dialogTitle,
            text = { LoudiusText(style = LoudiusTextStyle.ScreenContent, text = dialogText) },
            confirmButton = {
                LoudiusOutlinedButton(text = confirmText, onClick = onConfirmButtonClick)
            },
        )
    }
}

@Preview
@Composable
fun LoudiusErrorDialogPreview() {
    LoudiusTheme {
        LoudiusErrorDialog(onConfirmButtonClick = {})
    }
}
