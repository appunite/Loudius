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
    dialogTitle: String,
    dialogText: String
) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(text = dialogTitle) },
            text = { Text(text = dialogText) },
            confirmButton = {
                ConfirmButton {
                    onConfirmButtonClick()
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@Composable
private fun ConfirmButton(
    confirm: () -> Unit,
) {
    Button(
        onClick = { confirm() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.tertiary,
        ),
    ) {
        Text(text = stringResource(R.string.ok))
    }
}

@Preview
@Composable
fun LoudiusErrorDialogPreview() {
    LoudiusTheme {
        LoudiusErrorDialog(
            onConfirmButtonClick = { },
            dialogTitle = "Example title",
            dialogText = "Example text"
        )
    }
}
