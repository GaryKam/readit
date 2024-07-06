package io.github.garykam.readit.data.repository

import io.github.garykam.readit.data.model.RedditListing
import io.github.garykam.readit.data.model.RedditUser
import io.github.garykam.readit.data.model.Subreddit
import io.github.garykam.readit.data.model.SubredditPost
import io.github.garykam.readit.data.source.remote.RedditApiService
import io.github.garykam.readit.util.PreferenceUtil
import retrofit2.awaitResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedditApiRepository @Inject constructor(
    private val api: RedditApiService
) {
    private val accessToken: String
        get() = PreferenceUtil.getAccessToken()

    suspend fun getUser(): RedditUser? {
        return api.getRedditUser(accessToken).awaitResponse().body()
    }

    suspend fun getSubscribedSubreddits(): RedditListing<Subreddit>? {
        return api.getSubscribedSubredditsListing(accessToken).awaitResponse().body()
    }

    suspend fun getSubredditPosts(
        subreddit: String,
        order: String = "new",
        after: String? = null
    ): RedditListing<SubredditPost>? {
        return api.getSubredditPostsListing(accessToken, subreddit, order, after).awaitResponse().body()
    }

    suspend fun getUserSubreddit(
        user: String,
        where: String = "submitted",
        after: String? = null
    ): RedditListing<SubredditPost>? {
        return api.getUserSubredditListing(accessToken, user, where, after).awaitResponse().body()
    }
}
