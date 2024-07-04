package io.github.garykam.readit.data.model

import com.google.gson.annotations.SerializedName

data class Subreddit(
    val kind: String,
    val data: Data
) {
    data class Data(
        @SerializedName("title")
        val displayName: String,
        @SerializedName("display_name_prefixed")
        val prefixedName: String,
    )
}


