package io.github.garykam.readit.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import io.github.garykam.readit.R
import java.time.Instant

object PreferenceUtil {
    private lateinit var sharedPreferences: SharedPreferences
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_TOKEN_EXPIRATION = "token_expire"
    private const val KEY_SUBREDDIT = "subreddit"
    private const val KEY_SUBSCRIBED_SUBREDDITS = "subscribed_subreddits"
    private const val KEY_ORDER_SUFFIX = "_order"
    private const val KEY_DARK_THEME = "dark_theme"
    private const val DEFAULT_TOKEN = ""
    private const val DEFAULT_TOKEN_EXPIRATION = -1L

    fun init(context: Context) {
        sharedPreferences = EncryptedSharedPreferences.create(
            context.getString(R.string.app_name) + "_pref",
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun isLoggedIn(): Boolean {
        return getAccessToken() != DEFAULT_TOKEN
    }

    fun logOut() {
        setAccessToken(DEFAULT_TOKEN)
        setRefreshToken(DEFAULT_TOKEN)
        setTokenExpiration(DEFAULT_TOKEN_EXPIRATION)
        setSubreddit("")
        setSubscribedSubreddits(emptySet())
        setDarkTheme(false)
    }

    fun getAccessToken(): String {
        sharedPreferences.getString(KEY_ACCESS_TOKEN, DEFAULT_TOKEN)!!.run {
            return if (isEmpty()) DEFAULT_TOKEN else "Bearer $this"
        }
    }

    fun setAccessToken(accessToken: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    fun getRefreshToken(): String {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, DEFAULT_TOKEN).orEmpty()
    }

    fun setRefreshToken(refreshToken: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun isTokenExpired(): Boolean {
        sharedPreferences.getLong(KEY_TOKEN_EXPIRATION, DEFAULT_TOKEN_EXPIRATION).run {
            return if (this == DEFAULT_TOKEN_EXPIRATION) false else Instant.now().toEpochMilli() > this
        }
    }

    fun setTokenExpiration(expiresAt: Long) {
        sharedPreferences.edit().putLong(KEY_TOKEN_EXPIRATION, expiresAt).apply()
    }

    fun getSubreddit(): String {
        return sharedPreferences.getString(KEY_SUBREDDIT, "").orEmpty()
    }

    fun setSubreddit(subreddit: String) {
        sharedPreferences.edit().putString(KEY_SUBREDDIT, subreddit).apply()
    }

    fun getSubscribedSubreddits(): Set<String> {
        return sharedPreferences.getStringSet(KEY_SUBSCRIBED_SUBREDDITS, emptySet())?.toSortedSet().orEmpty()
    }

    fun setSubscribedSubreddits(subreddits: Set<String>) {
        sharedPreferences.edit().putStringSet(KEY_SUBSCRIBED_SUBREDDITS, subreddits).apply()
    }

    fun getPostOrder(subreddit: String): String {
        return sharedPreferences.getString(subreddit + KEY_ORDER_SUFFIX, "").orEmpty()
    }

    fun setPostOrder(subreddit: String, order: String) {
        sharedPreferences.edit().putString(subreddit + KEY_ORDER_SUFFIX, order).apply()
    }

    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    fun setDarkTheme(darkTheme: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, darkTheme).apply()
    }
}
