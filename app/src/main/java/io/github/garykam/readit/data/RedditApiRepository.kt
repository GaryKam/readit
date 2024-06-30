package io.github.garykam.readit.data

import io.github.garykam.readit.data.model.RedditUser
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
}
