package com.appunite.loudius.common

sealed class Screen(val route: String) {

    object Login : Screen("login_screen")

    object Repos : Screen("repos_screen")

    object PullRequests : Screen("pull_requests_screen")
}
