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

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.appunite.loudius.components.theme.LoudiusTheme

@Composable
fun LoudiusDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    dismissButton: @Composable (() -> Unit)? = null,
    /**
     * For text [LoudiusTextStyle.ScreenContent] should be used
     */
    text: @Composable (() -> Unit)? = null,

) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = {
            LoudiusText(style = LoudiusTextStyle.TitleLarge, text = title)
        },
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
    )
}

@Composable
@ShowkaseComposable(skip = true)
@Preview(group = "Dialogs")
fun LoudiusDialogSimplePreview() {
    LoudiusTheme {
        LoudiusDialog(
            onDismissRequest = { },
            title = "Title",
            confirmButton = {
                LoudiusOutlinedButton(text = "Confirm") {}
            },
        )
    }
}

@Composable
@ShowkaseComposable(skip = true)
@Preview(group = "Dialogs")
fun LoudiusDialogAdvancedPreview() {
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
