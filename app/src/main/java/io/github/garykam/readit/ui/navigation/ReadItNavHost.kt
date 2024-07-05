package io.github.garykam.readit.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.garykam.readit.ui.component.auth.AuthActivity
import io.github.garykam.readit.ui.component.profile.ProfileScreen
import io.github.garykam.readit.ui.component.subreddit.SubredditScreen
import io.github.garykam.readit.ui.component.subreddit.SubredditViewModel

@Composable
fun ReadItNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: Destination = Subreddit,
    subredditViewModel: SubredditViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = startDestination) {
        composable<Subreddit> {
            SubredditScreen(
                onProfileClick = { navController.navigate(Profile) },
                viewModel = subredditViewModel
            )
        }

        composable<Profile> {
            ProfileScreen(
                onBack = { navController.navigate(Subreddit) },
                onLogOut = { context.startActivity(Intent(context, AuthActivity::class.java)) }
            )
        }
    }
}
