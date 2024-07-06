package io.github.garykam.readit.data.model

data class RedditListing<T>(
    val data: Data<T>
) {
    data class Data<T>(
        val after: String,
        val children: List<T>
    )
}
