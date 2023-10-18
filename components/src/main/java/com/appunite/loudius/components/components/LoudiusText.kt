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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.appunite.loudius.components.theme.LoudiusTheme

enum class LoudiusTextStyle {
    ListHeader,
    ListHeaderWarning,
    ListItem,
    ListCaption,
    Button,
    TitleLarge,
    ScreenContent
}

@Composable
fun LoudiusText(
    text: String,
    modifier: Modifier = Modifier,
    style: LoudiusTextStyle = LoudiusTextStyle.ListCaption
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
        }
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
