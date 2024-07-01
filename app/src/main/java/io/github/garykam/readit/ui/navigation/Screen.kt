package io.github.garykam.readit.ui.navigation

sealed class Screen(
    val route: String
) {
    data object Subreddit : Screen("subreddit")
}