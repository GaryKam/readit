package io.github.garykam.readit.data.model

import com.google.gson.annotations.SerializedName

data class RedditUser(
    val name: String,
    @SerializedName("total_karma")
    val karma: String
)