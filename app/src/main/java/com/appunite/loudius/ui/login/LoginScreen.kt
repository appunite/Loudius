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

package com.appunite.loudius.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appunite.loudius.R
import com.appunite.loudius.common.Constants.AUTHORIZATION_URL
import com.appunite.loudius.ui.components.LoudiusOutlinedButton
import com.appunite.loudius.ui.components.LoudiusOutlinedButtonIcon
import com.appunite.loudius.ui.components.LoudiusOutlinedButtonStyle

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginImage()
        LoudiusOutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { startAuthorizing(context) },
            text = stringResource(id = R.string.login),
            style = LoudiusOutlinedButtonStyle.Large,
            icon = {
                LoudiusOutlinedButtonIcon(
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = stringResource(R.string.github_icon),
                )
            },

            )
    }
}

@Composable
fun LoginImage() {
    Image(
        painter = painterResource(id = R.drawable.loudius_logo),
        contentDescription = stringResource(
            R.string.login_screen,
        ),
    )
}

private fun startAuthorizing(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AUTHORIZATION_URL))
    context.startActivity(intent)
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}
