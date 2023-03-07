package com.appunite.loudius.ui.repos

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun ReposScreen(
    intent: Intent,
    viewModel: ReposViewModel = hiltViewModel(),
    navigateToList: () -> Unit,
) {
    val code = intent.data?.getQueryParameter("code")
    val rememberedCode = rememberUpdatedState(newValue = code)
    Column {
        Text(text = code ?: "code is already consumed")
    }
    LaunchedEffect(key1 = rememberedCode) {
        rememberedCode.value?.let {
            viewModel.getAccessToken(it)
            intent.data = null
            delay(1000)
            navigateToList()
        }
    }
}
