package com.appunite.loudius.presentation

sealed class ScreenRoute(val route: String) {

    object Login: ScreenRoute("login_screen")

    object ReposScreen: ScreenRoute("repos_screen")
}
