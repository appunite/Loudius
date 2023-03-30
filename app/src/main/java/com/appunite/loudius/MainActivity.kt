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
import androidx.navigation.navDeepLink
import com.appunite.loudius.common.Constants.REDIRECT_URL
import com.appunite.loudius.common.Screen
import com.appunite.loudius.ui.MainViewModel
import com.appunite.loudius.ui.loading.LoadingScreen
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
                            route = Screen.Repos.route,
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = REDIRECT_URL
                                },
                            ),
                        ) {
                            LoadingScreen(
                                intent = intent,
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
            getString(R.string.user_unauthorized_message),
            Toast.LENGTH_LONG
        ).show()
    }
}
