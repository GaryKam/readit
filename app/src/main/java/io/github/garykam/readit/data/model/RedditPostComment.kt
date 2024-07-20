package io.github.garykam.readit.data.model

import com.google.gson.annotations.SerializedName

data class RedditPostComment(
    val data: Data
) {
    data class Data(
        val title: String,
        @SerializedName("selftext_html")
        val header: String?,
        @SerializedName("body_html")
        val text: String?
    )
}
