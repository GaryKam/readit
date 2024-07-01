package io.github.garykam.readit.data.model

data class RedditListing<T>(
    val data: RedditListingData<T>
)

data class RedditListingData<T>(
    val children: List<T>
)
