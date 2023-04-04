package com.appunite.loudius.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.appunite.loudius.ui.theme.LoudiusTheme

enum class LoudiusTextStyle {
    ListHeader,
    ListHeaderWarning,
    ListItem,
    ListCaption,
    Button,
    TitleLarge,
    ScreenContent,
}

@Composable
fun LoudiusText(
    text: String,
    modifier: Modifier = Modifier,
    style: LoudiusTextStyle = LoudiusTextStyle.ListCaption,
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = when (style) {
            LoudiusTextStyle.ScreenContent -> TextAlign.Center
            else -> null
        },
        style = when (style) {
            LoudiusTextStyle.ListHeader -> MaterialTheme.typography.labelMedium
            LoudiusTextStyle.ListHeaderWarning -> MaterialTheme.typography.labelMedium
            LoudiusTextStyle.ListItem -> MaterialTheme.typography.bodyLarge
            LoudiusTextStyle.ListCaption -> MaterialTheme.typography.bodyMedium
            LoudiusTextStyle.Button -> MaterialTheme.typography.labelLarge
            LoudiusTextStyle.TitleLarge -> MaterialTheme.typography.titleLarge
            LoudiusTextStyle.ScreenContent -> MaterialTheme.typography.bodyLarge
        },
        color = when (style) {
            LoudiusTextStyle.ListHeaderWarning -> MaterialTheme.colorScheme.error
            LoudiusTextStyle.ListCaption -> MaterialTheme.colorScheme.onSurfaceVariant
            else -> MaterialTheme.colorScheme.onSurface
        },
    )
}

@Preview(showBackground = true)
@Composable
fun LoudiusTextStyles() {
    LoudiusTheme {
        Column {
            LoudiusTextStyle.values().forEach {
                LoudiusText(text = "$it Text", style = it)
            }
        }
    }
}
