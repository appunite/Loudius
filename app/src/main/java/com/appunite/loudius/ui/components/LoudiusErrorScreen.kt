package com.appunite.loudius.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appunite.loudius.R
import com.appunite.loudius.ui.theme.LoudiusTheme

@Composable
fun LoudiusErrorScreen(
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
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Preview(showSystemUi = true)
@Composable
fun LoudiusErrorScreenPreview() {
    LoudiusTheme {
        LoudiusErrorScreen(
            errorText = stringResource(id = R.string.error_dialog_text),
            buttonText = stringResource(R.string.try_again),
            onButtonClick = {},
        )
    }
}
