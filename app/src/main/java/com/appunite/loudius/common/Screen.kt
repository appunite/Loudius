package com.appunite.loudius.common

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    open val arguments: List<NamedNavArgument> = emptyList()

    object Login : Screen("login_screen")

    object Repos : Screen("repos_screen")

    object PullRequests : Screen("pull_requests_screen")

    object Reviewers :
        Screen("reviewers_screen/{pullRequestNumber}/{owner}/{repo}/{submissionDate}") {
        const val pullRequestNumberArg = "pull_request_number"
        const val ownerArg = "pull_request_number"
        const val repoArg = "pull_request_number"
        const val submissionDateArg = "pull_request_number"

        override val arguments: List<NamedNavArgument>
            get() {
                return listOf(
                    navArgument(pullRequestNumberArg) { type = NavType.StringType },
                    navArgument(ownerArg) { type = NavType.StringType },
                    navArgument(repoArg) { type = NavType.StringType },
                    navArgument(submissionDateArg) { type = NavType.StringType },
                )
            }
    }
}
