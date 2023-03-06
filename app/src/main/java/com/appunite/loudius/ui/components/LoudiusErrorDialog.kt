package com.appunite.loudius.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appunite.loudius.R
import com.appunite.loudius.ui.theme.LoudiusTheme

@Composable
fun LoudiusErrorDialog(
    onConfirmButtonClick: () -> Unit,
    dialogTitle: String = stringResource(id = R.string.error_dialog_title),
    dialogText: String = stringResource(id = R.string.error_dialog_text),
    confirmText: String = stringResource(R.string.ok),
) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(text = dialogTitle) },
            text = { Text(text = dialogText) },
            confirmButton = {
                ConfirmButton(
                    confirmText = confirmText,
                    confirm = onConfirmButtonClick,
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@Composable
private fun ConfirmButton(
    confirmText: String,
    confirm: () -> Unit,
) {
    Button(
        onClick = { confirm() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.tertiary,
        ),
    ) {
        Text(text = confirmText)
    }
}

@Preview
@Composable
fun LoudiusErrorDialogPreview() {
    LoudiusTheme {
        LoudiusErrorDialog(onConfirmButtonClick = {})
    }
}
