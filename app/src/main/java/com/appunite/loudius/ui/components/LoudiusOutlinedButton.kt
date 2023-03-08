package com.appunite.loudius.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun LoudiusOutlinedButton(
    text: String,
    iconPainter: Painter? = null,
    iconDescription: String? = null,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 46.dp),
    ) {
        if (iconPainter != null) {
            Icon(
                painter = iconPainter,
                contentDescription = iconDescription,
                tint = Color.Black,
            )
        }
        Text(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
            text = text,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}
