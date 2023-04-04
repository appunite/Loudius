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

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appunite.loudius.R
import com.appunite.loudius.ui.theme.LoudiusTheme

enum class LoudiusOutlinedButtonStyle {
    Large,
    Regular,
}

@Composable
fun LoudiusOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    style: LoudiusOutlinedButtonStyle = LoudiusOutlinedButtonStyle.Regular,
    onClick: () -> Unit,
) {
    OutlinedButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
            .applyIf(style == LoudiusOutlinedButtonStyle.Large) { padding(horizontal = 46.dp) },
    ) {
        icon?.invoke()

        LoudiusText(
            text = text,
            modifier = Modifier
                .applyIf<Modifier>(style == LoudiusOutlinedButtonStyle.Large) { padding(8.dp) }
                .applyIf(style == LoudiusOutlinedButtonStyle.Regular && icon != null) { padding(start = 8.dp) },
            style = LoudiusTextStyle.Button,
        )
    }
}

inline fun <T> T.applyIf(predicate: Boolean, block: T.() -> T): T {
    return if (predicate) block() else this
}

@Composable
fun LoudiusOutlinedButtonIcon(
    painter: Painter,
    contentDescription: String
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = Color.Black,
    )
}

@Composable
@Preview(showBackground = true)
fun LoudiusOutlinedButtonPreview() {
    LoudiusTheme {
        LoudiusOutlinedButton(
            onClick = { },
            text = "Some button",
        )
    }
}
@Composable
@Preview(showBackground = true)
fun LoudiusOutlinedButtonLargePreview() {
    LoudiusTheme {
        LoudiusOutlinedButton(
            onClick = { },
            text = "Some button",
            style = LoudiusOutlinedButtonStyle.Large,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoudiusOutlinedButtonWithIconPreview() {
    LoudiusTheme {
        LoudiusOutlinedButton(
            onClick = { },
            text = "Log In",
            icon = {
                LoudiusOutlinedButtonIcon(
                    painter = painterResource(id = R.drawable.ic_github),
                    "Github Icon"
                )
            }
        )
    }
}
@Composable
@Preview(showBackground = true)
fun LoudiusOutlinedButtonDisabledPreview() {
    LoudiusTheme {
        LoudiusOutlinedButton(
            onClick = { },
            text = "Disabled button",
            enabled = false,
            icon = {
                LoudiusOutlinedButtonIcon(
                    painter = painterResource(id = R.drawable.ic_github),
                    "Github Icon"
                )
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoudiusOutlinedButtonWithIconLargePreview() {
    LoudiusTheme {
        LoudiusOutlinedButton(
            onClick = { },
            text = "Log In",
            style = LoudiusOutlinedButtonStyle.Large,
            icon = {
                LoudiusOutlinedButtonIcon(
                    painter = painterResource(id = R.drawable.ic_github),
                    "Github Icon"
                )
            }
        )
    }
}