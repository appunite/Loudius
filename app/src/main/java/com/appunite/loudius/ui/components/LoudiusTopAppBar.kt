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
    onClickBackArrow: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { onClickBackArrow() }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = stringResource(R.string.back_button)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    )
}

@Preview
@Composable
fun LoudiusTopAppBar() {
    LoudiusTheme {
        LoudiusTopAppBar(
            onClickBackArrow = {},
            title = "Loudius"
        )
    }
}
