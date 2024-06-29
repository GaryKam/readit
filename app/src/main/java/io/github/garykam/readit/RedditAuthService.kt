package io.github.garykam.readit

import okhttp3.FormBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RedditAuthService {
    @Headers(Constants.USER_AGENT_HEADER)
    @POST("v1/access_token")
    fun getAccessToken(
        @Header("Authorization") credentials: String,
        @Body body: FormBody
    ): Call<RedditAuthResponse>

    companion object {
        private const val AUTH_BASE_URL = "https://www.reddit.com/api/"

        fun create(): RedditAuthService {
            return Retrofit.Builder()
                .baseUrl(AUTH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RedditAuthService::class.java)
        }
    }
}
