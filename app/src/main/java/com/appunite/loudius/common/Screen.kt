package com.appunite.loudius.common

sealed class Screen(val route: String) {

    object Login : Screen("login_screen")

    object Repos : Screen("repos_screen")
}
