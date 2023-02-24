package com.appunite.loudius

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.appunite.loudius.common.Constants.REDIRECT_URL
import com.appunite.loudius.common.Screen
import com.appunite.loudius.presentation.login.LoginScreen
import com.appunite.loudius.presentation.repos.ReposScreen
import com.appunite.loudius.ui.theme.LoudiusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoudiusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route
                    ) {
                        composable(route = Screen.Login.route) {
                            LoginScreen(
                                context = this@MainActivity
                            )
                        }
                        composable(
                            route = Screen.Repos.route,
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = REDIRECT_URL
                                },
                            ),
                        ) {
                            ReposScreen(intent = intent)
                        }
                    }
                }
            }
        }
    }
}
