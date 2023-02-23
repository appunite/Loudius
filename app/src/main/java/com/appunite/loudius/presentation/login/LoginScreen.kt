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
import com.appunite.loudius.common.Constants.AUTH_URL
import com.appunite.loudius.common.Constants.CLIENT_ID

@Composable
fun LoginScreen(
    context: Context
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                val url = "$AUTH_URL/login/oauth/authorize?client_id=$CLIENT_ID"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        ) {
            Text(text = "Login")
        }
    }
}

@Composable
fun ExampleScreen(intent: Intent) {
    Text(text = intent.data?.getQueryParameter("code") ?: "empty code")
}
