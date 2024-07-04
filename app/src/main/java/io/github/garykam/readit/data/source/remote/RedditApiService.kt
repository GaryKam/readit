package io.github.garykam.readit.data.source.remote

import io.github.garykam.readit.data.model.RedditListing
import io.github.garykam.readit.data.model.RedditUser
import io.github.garykam.readit.data.model.Subreddit
import io.github.garykam.readit.data.model.SubredditPost
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApiService {
    @Headers(USER_AGENT_HEADER)
    @GET("api/v1/me")
    fun getUser(
        @Header("Authorization") bearer: String,
        @Query("raw_json") rawJson: Int = 1
    ): Call<RedditUser>

    @Headers(USER_AGENT_HEADER)
    @GET("subreddits/mine/subscriber")
    fun getSubscribedSubreddits(
        @Header("Authorization") bearer: String,
        @Query("raw_json") rawJson: Int = 1
    ): Call<RedditListing<Subreddit>>

    @Headers(USER_AGENT_HEADER)
    @GET("r/{subreddit}/{order}")
    fun getSubredditPosts(
        @Header("Authorization") bearer: String,
        @Path("subreddit") subreddit: String,
        @Path("order") order: String,
        @Query("raw_json") rawJson: Int = 1
    ): Call<RedditListing<SubredditPost>>

    @Headers(USER_AGENT_HEADER)
    @GET("user/{user}/{where}")
    fun getUserSubreddit(
        @Header("Authorization") bearer: String,
        @Path("user") user: String,
        @Path("where") where: String,
        @Query("raw_json") rawJson: Int = 1
    ): Call<RedditListing<SubredditPost>>

    companion object {
        private const val USER_AGENT_HEADER = "User-Agent: android:io.github.garykam.readit:v0.0.1 (by /u/garyeeb)"
        private const val API_BASE_URL = "https://oauth.reddit.com/"

        fun create(): RedditApiService {
            return Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RedditApiService::class.java)
        }
    }
}
