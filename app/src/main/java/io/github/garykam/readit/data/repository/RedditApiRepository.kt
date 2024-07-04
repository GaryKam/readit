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
        return api.getUser(accessToken).awaitResponse().body()
    }

    suspend fun getSubscribedSubredditsListing(): RedditListing<Subreddit>? {
        return api.getSubscribedSubreddits(accessToken).awaitResponse().body()
    }

    suspend fun getSubredditPosts(
        subreddit: String,
        order: String = "new"
    ): RedditListing<SubredditPost>? {
        return api.getSubredditPosts(accessToken, subreddit, order).awaitResponse().body()
    }

    suspend fun getUserSubreddit(
        user: String,
        where: String = "submitted"
    ): RedditListing<SubredditPost>? {
        return api.getUserSubreddit(accessToken, user, where).awaitResponse().body()
    }
}
