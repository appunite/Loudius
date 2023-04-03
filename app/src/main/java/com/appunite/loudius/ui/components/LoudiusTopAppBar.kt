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

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appunite.loudius.R
import com.appunite.loudius.ui.theme.LoudiusTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoudiusTopAppBar(
    title: String,
    onClickBackArrow: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        navigationIcon = {
            if (onClickBackArrow != null) {
                IconButton(onClick = onClickBackArrow) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = stringResource(R.string.back_button),
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Preview
@Composable
fun LoudiusTopAppBar() {
    LoudiusTheme {
        LoudiusTopAppBar(
            onClickBackArrow = {},
            title = "Loudius",
        )
    }
}

// Suggestion: add this, so we can verify UI with different states
@Preview
@Composable
fun LoudiusTopAppBarWithoutBackButton() {
    LoudiusTheme {
        LoudiusTopAppBar(
            title = "Loudius",
        )
    }
}
