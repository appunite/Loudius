package com.appunite.loudius.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appunite.loudius.R
import com.appunite.loudius.common.Constants.AUTH_API_URL
import com.appunite.loudius.common.Constants.AUTH_PATH
import com.appunite.loudius.common.Constants.CLIENT_ID
import com.appunite.loudius.common.Constants.NAME_PARAM_CLIENT_ID
import com.appunite.loudius.common.Constants.SCOPE_PARAM
import com.appunite.loudius.ui.theme.Pink40

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginImage()
        LoginButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 46.dp),
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

@Composable
fun LoginButton(modifier: Modifier) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = { startAuthorizing(context) },
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_github),
            contentDescription = stringResource(R.string.github_icon),
            tint = Color.Black,
        )
        Text(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
            text = stringResource(id = R.string.login),
            color = Pink40,
        )
    }
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
