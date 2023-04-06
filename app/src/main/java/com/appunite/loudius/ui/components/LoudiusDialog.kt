package com.appunite.loudius.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.appunite.loudius.ui.theme.LoudiusTheme

@Composable
fun LoudiusDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    dismissButton: @Composable (() -> Unit)? = null,
    /**
     * For text {@see LoudiusTextStyle#ScreenContent} should be used
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
        dismissButton = dismissButton
    )
}

@Composable
@Preview
fun LoudiusDialogSimplePreview() {
    LoudiusTheme {
        LoudiusDialog(
            onDismissRequest = { },
            title = "Title",
            confirmButton = {
                LoudiusOutlinedButton(text = "Confirm") {
                }
            },
        )
    }
}
@Composable
@Preview
fun LoudiusDialogAdvancedPreview() {
    LoudiusTheme {
        LoudiusDialog(
            onDismissRequest = { },
            title = "Title",
            text = {
                LoudiusText(
                    style = LoudiusTextStyle.ScreenContent,
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse dapibus elit justo, at pharetra nulla mattis vel. Integer gravida tortor sed fringilla viverra. Duis scelerisque ante neque, a pretium eros."
                )
            },
            confirmButton = {
                LoudiusOutlinedButton(text = "Confirm") {
                }
            },
            dismissButton = {
                LoudiusOutlinedButton(text = "Dismiss") {
                }
            }
        )
    }
}