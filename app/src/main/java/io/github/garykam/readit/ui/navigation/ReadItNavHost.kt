package io.github.garykam.readit.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.garykam.readit.ui.component.auth.AuthActivity
import io.github.garykam.readit.ui.component.main.AppBarState
import io.github.garykam.readit.ui.component.profile.ProfileScreen
import io.github.garykam.readit.ui.component.subreddit.SubredditScreen
import io.github.garykam.readit.ui.component.subreddit.SubredditViewModel
import io.github.garykam.readit.ui.component.redditpost.RedditPostScreen

@Composable
fun ReadItNavHost(
    onAppBarStateUpdate: (AppBarState) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Destination = Subreddit,
    subredditViewModel: SubredditViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Subreddit> {
            SubredditScreen(
                onAppBarStateUpdate = onAppBarStateUpdate,
                onNavigateToRedditPost = { subreddit, postId -> navController.navigate(RedditPost(subreddit, postId)) },
                onNavigateToProfile = { navController.navigate(Profile) },
                modifier = Modifier.fillMaxSize(),
                viewModel = subredditViewModel
            )
        }

        composable<RedditPost> { backStackEntry ->
            val route = backStackEntry.toRoute() as RedditPost
            RedditPostScreen(
                subreddit = route.subreddit,
                postId = route.postId,
                onAppBarStateUpdate = onAppBarStateUpdate,
                onNavigateBack = { navController.navigate(Subreddit) }
            )
        }

        composable<Profile> {
            ProfileScreen(
                onAppBarStateUpdate = onAppBarStateUpdate,
                onNavigateBack = { navController.navigate(Subreddit) },
                onNavigateToAuth = { context.startActivity(Intent(context, AuthActivity::class.java)) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
