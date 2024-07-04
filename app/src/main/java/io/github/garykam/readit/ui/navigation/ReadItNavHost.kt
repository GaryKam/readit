package io.github.garykam.readit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.garykam.readit.ui.component.subreddit.SubredditScreen

@Composable
fun ReadItNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: Destination = Subreddit,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable<Subreddit> {
            SubredditScreen()
        }
    }
}
