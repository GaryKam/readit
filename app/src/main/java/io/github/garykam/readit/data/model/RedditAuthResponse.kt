package io.github.garykam.readit.data.model

import com.google.gson.annotations.SerializedName

data class RedditAuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiration: Int
)
