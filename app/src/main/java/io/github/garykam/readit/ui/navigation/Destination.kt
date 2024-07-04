package io.github.garykam.readit.ui.navigation

import kotlinx.serialization.Serializable

sealed class Destination

@Serializable
data object Subreddit : Destination()
