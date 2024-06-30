package io.github.garykam.readit.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import io.github.garykam.readit.R

object PreferenceUtil {
    private lateinit var sharedPreferences: SharedPreferences
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val DEFAULT_TOKEN = ""

    fun init(context: Context) {
        sharedPreferences = EncryptedSharedPreferences.create(
            context.getString(R.string.app_name) + "_pref",
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getAccessToken(): String {
        sharedPreferences.getString(KEY_ACCESS_TOKEN, DEFAULT_TOKEN)!!.run {
            return if (isEmpty()) DEFAULT_TOKEN else "Bearer $this"
        }
    }

    fun setAccessToken(accessToken: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }
}
