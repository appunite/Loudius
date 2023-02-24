package com.appunite.loudius.presentation.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.appunite.loudius.R
import com.appunite.loudius.common.Constants.AUTH_PATH
import com.appunite.loudius.common.Constants.BASE_URL
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.common.Constants.NAME_PARAM_CLIENT_ID

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = { startAuthorizing(context) },
        ) {
            Text(text = stringResource(id = R.string.login))
        }
    }
}

private fun startAuthorizing(context: Context) {
    val url = buildAuthorizationUrl()
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

private fun buildAuthorizationUrl() = BASE_URL + AUTH_PATH + NAME_PARAM_CLIENT_ID + CLIENT_ID
