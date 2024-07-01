package io.github.garykam.readit.data.model

import com.google.gson.annotations.SerializedName

data class Subreddit(
    val kind: String,
    val data: SubredditData
)

data class SubredditData(
    @SerializedName("title")
    val displayName: String,
    @SerializedName("display_name_prefixed")
    val prefixedName: String,
)
