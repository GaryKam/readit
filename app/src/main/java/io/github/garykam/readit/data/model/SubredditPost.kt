package io.github.garykam.readit.data.model

import com.google.gson.annotations.SerializedName

data class SubredditPost(
    val kind: String,
    val data: Data
) {
    data class Data(
        @SerializedName("name")
        val id: String,
        val title: String,
        val score: Int,
        val created: Long,
        @SerializedName("num_comments")
        val comments: Int
    )
}