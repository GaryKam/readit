package io.github.garykam.readit.data.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import io.github.garykam.readit.data.source.remote.RedditPostCommentAdapterFactory

data class RedditPostComment(
    val data: Data
) {
    data class Data(
        val title: String,
        @SerializedName("selftext_html")
        val header: String?,
        @SerializedName("body_html")
        val text: String?,
        @JsonAdapter(RedditPostCommentAdapterFactory::class)
        val replies: RedditListing<RedditPostComment>?
    )
}