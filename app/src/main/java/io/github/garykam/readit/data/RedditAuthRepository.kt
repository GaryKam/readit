package io.github.garykam.readit.data

import android.net.Uri
import android.util.Log
import io.github.garykam.readit.data.model.RedditAuthResponse
import io.github.garykam.readit.data.source.remote.RedditAuthService
import io.github.garykam.readit.util.PreferenceUtil
import okhttp3.Credentials
import okhttp3.FormBody
import retrofit2.awaitResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedditAuthRepository @Inject constructor(
    private val auth: RedditAuthService
) {
    val authUrl: Uri
        get() {
            state = getRandomState()
            return Uri.parse(String.format(AUTH_URL, CLIENT_ID, state, REDIRECT_URI, DURATION, SCOPE))
        }
    private var state = ""

    suspend fun fetchAuthResponse(code: String, state: String): RedditAuthResponse {
        if (state != this.state) {
            throw Exception("Invalid state")
        }

        val credentials = Credentials.basic(CLIENT_ID, "")
        val body = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", REDIRECT_URI)
            .build()

        val response = auth.getAuthResponse(credentials, body).awaitResponse()

        if (response.isSuccessful) {
            Log.d("RedditAuthRepository", "Successfully retrieved response")
            return response.body()!!
        } else {
            throw Exception(response.errorBody().toString())
        }
    }

    suspend fun fetchAuthResponse(): RedditAuthResponse {
        val credentials = Credentials.basic(CLIENT_ID, "")
        val body = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", PreferenceUtil.getRefreshToken())
            .build()

        val response = auth.getAuthResponse(credentials, body).awaitResponse()

        if (response.isSuccessful) {
            Log.d("RedditAuthRepository", "Successfully retrieved response")
            return response.body()!!
        } else {
            throw Exception(response.errorBody().toString())
        }
    }

    private fun getRandomState(): String {
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(15) { chars.random() }.joinToString("")
    }

    companion object {
        private const val AUTH_URL = "https://www.reddit.com/api/v1/authorize?client_id=%s&response_type=code&state=%s&redirect_uri=%s&duration=%s&scope=%s"
        private const val CLIENT_ID = "2tTU7W_Ode727mcjbyMgKw"
        private const val REDIRECT_URI = "readit://auth"
        private const val DURATION = "permanent"
        private const val SCOPE = "identity"
    }
}
