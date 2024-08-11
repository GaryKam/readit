package io.github.garykam.readit.ui.component.auth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.R
import io.github.garykam.readit.data.model.RedditAuthResult
import io.github.garykam.readit.data.repository.RedditAuthRepository
import io.github.garykam.readit.util.PreferenceUtil
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: RedditAuthRepository
) : ViewModel() {
    fun launchAuthBrowser(context: Context) {
        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder().setToolbarColor(Color.WHITE).build())
            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, CustomTabColorSchemeParams.Builder().setToolbarColor(Color.BLACK).build())
            .setShowTitle(false)
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .setBookmarksButtonEnabled(false)
            .setDownloadButtonEnabled(false)
            .setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
            .setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .build()
            .run {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_NO_HISTORY)
                launchUrl(context, repository.authUrl)
            }
    }

    fun retrieveAccessToken(error: String?, state: String?, code: String?): LiveData<RedditAuthResult> {
        val authResult = MutableLiveData<RedditAuthResult>()

        if (!error.isNullOrEmpty() || state.isNullOrEmpty() || code.isNullOrEmpty()) {
            authResult.value = RedditAuthResult.Error("Failed to authenticate: $error")
            return authResult
        }

        viewModelScope.launch {
            val authResponse = repository.fetchAuthResponse(code, state)

            if (authResponse.accessToken.isNotEmpty()) {
                PreferenceUtil.setAccessToken(authResponse.accessToken)
                PreferenceUtil.setRefreshToken(authResponse.refreshToken)
                PreferenceUtil.setTokenExpiration(Instant.now().plusSeconds(authResponse.expiresIn).toEpochMilli())
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
                PreferenceUtil.setTokenExpiration(Instant.now().plusSeconds(authResponse.expiresIn).toEpochMilli())
                authResult.value = RedditAuthResult.Success
            } else {
                authResult.value = RedditAuthResult.Error("Failed to refresh access token")
            }
        }

        return authResult
    }
}
