package io.github.garykam.readit.data.source.remote

import com.google.gson.GsonBuilder
import io.github.garykam.readit.data.model.RedditListing
import io.github.garykam.readit.data.model.RedditPost
import io.github.garykam.readit.data.model.RedditPostComment
import io.github.garykam.readit.data.model.RedditUser
import io.github.garykam.readit.data.model.Subreddit
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
    fun getRedditUser(
        @Header("Authorization") bearer: String,
        @Query("raw_json") rawJson: Int = 1
    ): Call<RedditUser>

    @Headers(USER_AGENT_HEADER)
    @GET("subreddits/mine/subscriber")
    fun getSubscribedSubredditsListing(
        @Header("Authorization") bearer: String,
        @Query("raw_json") rawJson: Int = 1
    ): Call<RedditListing<Subreddit>>

    @Headers(USER_AGENT_HEADER)
    @GET("r/{subreddit}/{order}")
    fun getSubredditPostsListing(
        @Header("Authorization") bearer: String,
        @Path("subreddit") subreddit: String,
        @Path("order") order: String,
        @Query("after") after: String?,
        @Query("raw_json") rawJson: Int = 1
    ): Call<RedditListing<RedditPost>>

    @Headers(USER_AGENT_HEADER)
    @GET("user/{user}/{where}")
    fun getUserProfilePostsListing(
        @Header("Authorization") bearer: String,
        @Path("user") user: String,
        @Path("where") where: String,
        @Query("after") after: String?,
        @Query("raw_json") rawJson: Int = 1
    ): Call<RedditListing<RedditPost>>

    @Headers(USER_AGENT_HEADER)
    @GET("r/{subreddit}/comments/{article}")
    fun getRedditPostCommentsListing(
        @Header("Authorization") bearer: String,
        @Path("subreddit") subreddit: String,
        @Path("article") postId: String,
        @Query("raw_json") rawJson: Int = 1
    ): Call<List<RedditListing<RedditPostComment>>>

    companion object {
        private const val USER_AGENT_HEADER = "User-Agent: android:io.github.garykam.readit:v0.0.1 (by /u/garyeeb)"
        private const val API_BASE_URL = "https://oauth.reddit.com/"

        fun create(): RedditApiService {
            return Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().registerTypeAdapterFactory(RedditPostCommentAdapterFactory()).create()
                    )
                )
                .build()
                .create(RedditApiService::class.java)
        }
    }
}
