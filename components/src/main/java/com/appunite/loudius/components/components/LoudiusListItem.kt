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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appunite.loudius.components.components.utils.bottomBorder
import com.appunite.loudius.components.R as componentsR

@Composable
fun resolveListItemBackgroundColor(index: Int): Color =
    if (index % 2 == 0) MaterialTheme.colorScheme.onSurface.copy(0.08f) else MaterialTheme.colorScheme.surface

@Composable
fun LoudiusListItem(
    index: Int,
    modifier: Modifier = Modifier,
    icon: @Composable (Modifier) -> Unit = {},
    content: @Composable (Modifier) -> Unit,
    action: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(resolveListItemBackgroundColor(index))
            .bottomBorder(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(16.dp),
    ) {
        icon(Modifier.align(Alignment.CenterVertically))
        content(
            Modifier
                .weight(1f)
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically),
        )
        action()
    }
}

@Composable
fun LoudiusListIcon(modifier: Modifier, painter: Painter, contentDescription: String) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
    )
}

@Composable
@Preview(showBackground = true)
fun LoudiusListItemManyWithAllItemsPreview() {
    Column {
        LoudiusListItemExample(0)
        LoudiusListItemExample(1)
        LoudiusListItemExample(2)
    }
}

@Composable
@Preview(showBackground = true)
fun LoudiusListItemJustContentPreview() {
    LoudiusListItem(
        index = 0,
        content = { modifier ->
            LoudiusText(
                text = "Text",
                modifier = modifier,
                style = LoudiusTextStyle.ListItem,
            )
        },
    )
}

@Composable
@Preview(showBackground = true)
fun LoudiusListItemMultiplePreview() {
    LoudiusListItem(
        index = 0,
        content = { modifier ->
            Column(modifier = modifier) {
                LoudiusText(
                    text = "Title",
                    style = LoudiusTextStyle.ListItem,
                )
                LoudiusText(
                    text = "Caption",
                    style = LoudiusTextStyle.ListCaption,
                )
            }
        },
    )
}

@Composable
@Preview(showBackground = true)
fun LoudiusListItemWithHeaderPreview() {
    LoudiusListItem(
        index = 0,
        content = { modifier ->
            Column(modifier = modifier) {
                LoudiusText(
                    text = "Header text",
                    style = LoudiusTextStyle.ListHeader,
                )
                LoudiusText(
                    text = "Title",
                    style = LoudiusTextStyle.ListItem,
                )
            }
        },
    )
}

@Composable
@Preview(showBackground = true)
fun LoudiusListItemContentAndActionPreview() {
    LoudiusListItem(
        index = 0,
        content = { modifier ->
            LoudiusText(
                text = "Text",
                modifier = modifier,
                style = LoudiusTextStyle.ListItem,
            )
        },
        action = {
            LoudiusOutlinedButton(text = "Button") {}
        },
    )
}

@Composable
@Preview(showBackground = true)
fun LoudiusListItemContentAndIconPreview() {
    LoudiusListItem(
        index = 0,
        icon = { modifier ->
            LoudiusListIcon(
                painter = painterResource(id = componentsR.drawable.components_person_outline_24px),
                contentDescription = "Test",
                modifier = modifier,
            )
        },
        content = { modifier ->
            LoudiusText(
                text = "Text",
                modifier = modifier,
                style = LoudiusTextStyle.ListItem,
            )
        },
    )
}

@Composable
private fun LoudiusListItemExample(index: Int) {
    LoudiusListItem(
        index = index,
        icon = { modifier ->
            LoudiusListIcon(
                modifier = modifier,
                painter = painterResource(id = componentsR.drawable.components_person_outline_24px),
                contentDescription = "Test",
            )
        },
        content = { modifier ->
            LoudiusText(
                text = "Text",
                modifier = modifier,
                style = LoudiusTextStyle.ListItem,
            )
        },
        action = {
            LoudiusOutlinedButton(text = "Button", onClick = {})
        },
    )
}
