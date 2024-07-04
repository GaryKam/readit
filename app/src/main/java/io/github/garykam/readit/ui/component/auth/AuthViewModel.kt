package io.github.garykam.readit.ui.component.auth

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.repository.RedditAuthRepository
import io.github.garykam.readit.data.model.RedditAuthResult
import io.github.garykam.readit.util.PreferenceUtil
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: RedditAuthRepository
) : ViewModel() {
    fun launchAuthBrowser(context: Context) {
        CustomTabsIntent.Builder().build().run {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_NO_HISTORY)
            launchUrl(context, repository.authUrl)
        }
    }

    fun retrieveAccessToken(code: String, state: String): LiveData<RedditAuthResult> {
        val authResult = MutableLiveData<RedditAuthResult>()

        viewModelScope.launch {
            val authResponse = repository.fetchAuthResponse(code, state)

            if (authResponse.accessToken.isNotEmpty()) {
                PreferenceUtil.setAccessToken(authResponse.accessToken)
                PreferenceUtil.setRefreshToken(authResponse.refreshToken)
                PreferenceUtil.setTokenExpiration(authResponse.expiresIn)
                authResult.value = RedditAuthResult.Success
            } else {
                authResult.value = RedditAuthResult.Error("Failed to retrieve access token")
            }
        }

        return authResult
    }

    fun refreshAccessToken(): LiveData<RedditAuthResult> {
        val authResult = MutableLiveData<RedditAuthResult>()

        viewModelScope.launch {
            val authResponse = repository.fetchAuthResponse()

            if (authResponse.accessToken.isNotEmpty()) {
                PreferenceUtil.setAccessToken(authResponse.accessToken)
                PreferenceUtil.setTokenExpiration(authResponse.expiresIn)
                authResult.value = RedditAuthResult.Success
            } else {
                authResult.value = RedditAuthResult.Error("Failed to refresh access token")
            }
        }

        return authResult
    }
}