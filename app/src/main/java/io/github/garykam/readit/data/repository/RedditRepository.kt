package io.github.garykam.readit.data.repository

import io.github.garykam.readit.data.model.RedditListing
import io.github.garykam.readit.data.model.RedditUser
import io.github.garykam.readit.data.model.Subreddit
import io.github.garykam.readit.data.model.RedditPost
import io.github.garykam.readit.data.model.RedditPostComment
import io.github.garykam.readit.data.source.remote.RedditApiService
import io.github.garykam.readit.util.PreferenceUtil
import retrofit2.awaitResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedditRepository @Inject constructor(
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

    suspend fun getPostsFromSubreddit(
        subreddit: String,
        postOrder: String,
        topPostOrder: String?,
        after: String?,
    ): RedditListing<RedditPost>? {
        return api.getSubredditPostsListing(accessToken, subreddit, postOrder, topPostOrder, after).awaitResponse().body()
    }

    suspend fun getPostsFromUserProfile(
        user: String,
        where: String = "submitted",
        after: String? = null
    ): RedditListing<RedditPost>? {
        return api.getUserProfilePostsListing(accessToken, user, where, after).awaitResponse().body()
    }

    suspend fun getCommentsFromId(
        subreddit: String,
        postId: String
    ): List<RedditListing<RedditPostComment>>? {
        return api.getRedditPostCommentsListing(accessToken, subreddit, postId).awaitResponse().body()
    }
}
