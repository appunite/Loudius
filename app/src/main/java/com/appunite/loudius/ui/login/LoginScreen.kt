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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appunite.loudius.R
import com.appunite.loudius.common.Constants.AUTH_API_URL
import com.appunite.loudius.common.Constants.AUTH_PATH
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.common.Constants.NAME_PARAM_CLIENT_ID
import com.appunite.loudius.common.Constants.SCOPE_PARAM
import com.appunite.loudius.ui.components.LoudiusOutlinedButton

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
            onClick = { startAuthorizing(context) },
            text = stringResource(id = R.string.login),
            iconPainter = painterResource(id = R.drawable.ic_github),
            iconDescription = stringResource(R.string.github_icon),
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
    val url = buildAuthorizationUrl()
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

private fun buildAuthorizationUrl() = AUTH_API_URL + AUTH_PATH + NAME_PARAM_CLIENT_ID + CLIENT_ID + SCOPE_PARAM

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}
