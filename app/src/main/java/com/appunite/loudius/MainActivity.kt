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

package com.appunite.loudius

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appunite.loudius.common.Screen
import com.appunite.loudius.ui.MainViewModel
import com.appunite.loudius.ui.authenticating.AuthenticatingScreen
import com.appunite.loudius.ui.login.LoginScreen
import com.appunite.loudius.ui.pullrequests.PullRequestsScreen
import com.appunite.loudius.ui.reviewers.ReviewersScreen
import com.appunite.loudius.ui.theme.LoudiusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoudiusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    LaunchedEffect(viewModel.state.authFailureEvent) {
                        navigateToLoginOnAuthFailure(navController)
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                    ) {
                        composable(route = Screen.Login.route) {
                            LoginScreen()
                        }
                        composable(
                            route = Screen.Authenticating.route,
                            deepLinks = Screen.Authenticating.deepLinks,
                        ) {
                            AuthenticatingScreen(
                                onNavigateToPullRequest = {
                                    navController.navigate(Screen.PullRequests.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                            )
                        }
                        composable(route = Screen.PullRequests.route) {
                            PullRequestsScreen { owner, repo, pullRequestNumber, submissionTime ->
                                val route = Screen.Reviewers.constructRoute(
                                    owner = owner,
                                    repo = repo,
                                    pullRequestNumber = pullRequestNumber,
                                    submissionDate = submissionTime,
                                )
                                navController.navigate(route)
                            }
                        }
                        composable(
                            route = Screen.Reviewers.route,
                            arguments = Screen.Reviewers.arguments,
                        ) {
                            ReviewersScreen { navController.popBackStack() }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToLoginOnAuthFailure(navController: NavHostController) {
        if (viewModel.state.authFailureEvent != null) {
            showAuthFailureToast()
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
            viewModel.onAuthFailureHandled()
        }
    }

    private fun showAuthFailureToast() {
        Toast.makeText(
            this@MainActivity,
            getString(R.string.common_user_unauthorized_error_message),
            Toast.LENGTH_LONG,
        ).show()
    }
}
