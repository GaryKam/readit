package io.github.garykam.readit.ui.navigation

import kotlinx.serialization.Serializable

sealed class Destination

@Serializable
data object Subreddit : Destination()

@Serializable
data class RedditPost(val postId: String) : Destination()

@Serializable
data object Profile : Destination()
