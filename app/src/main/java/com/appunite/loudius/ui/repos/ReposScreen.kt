package com.appunite.loudius.ui.repos

import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReposScreen(
    intent: Intent,
    viewModel: ReposViewModel = hiltViewModel(),
) {
    val code = intent.data?.getQueryParameter("code")
    Text(text = code ?: "empty code")
    code?.let {
        viewModel.getAccessToken(code)
    }
}
