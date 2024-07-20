package io.github.garykam.readit.data.model

import com.google.gson.annotations.SerializedName

data class RedditPost(
    val data: Data
) {
    data class Data(
        @SerializedName("name")
        val id: String,
        val author: String,
        val title: String,
        @SerializedName("selftext_html")
        val text: String?,
        val score: Int,
        val created: Long,
        @SerializedName("num_comments")
        val comments: Int
    )
}
