package io.github.garykam.readit

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: RedditAuthRepository
) : ViewModel() {
    fun launchAuthBrowser(context: Context) {
        CustomTabsIntent.Builder().build().run {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchUrl(context, authRepository.authUrl)
        }
    }

    fun getAccessToken(code: String, state: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val accessToken = authRepository.fetchAccessToken(code, state)
            Log.d("MainViewModel", "access token: $accessToken")
        }
    }
}
