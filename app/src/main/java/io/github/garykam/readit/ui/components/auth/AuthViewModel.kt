package io.github.garykam.readit.ui.components.auth

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.RedditAuthRepository
import io.github.garykam.readit.data.model.RedditAuthResult
import io.github.garykam.readit.util.PreferenceUtil
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: RedditAuthRepository
) : ViewModel() {
    fun launchAuthBrowser(context: Context) {
        CustomTabsIntent.Builder().build().run {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_NO_HISTORY)
            launchUrl(context, authRepository.authUrl)
        }
    }

    fun getAccessToken(code: String, state: String): LiveData<RedditAuthResult> {
        val authResult = MutableLiveData<RedditAuthResult>()

        viewModelScope.launch {
            val authResponse = authRepository.fetchAuthResponse(code, state)

            if (authResponse.accessToken.isNotEmpty()) {
                PreferenceUtil.setAccessToken(authResponse.accessToken)
                authResult.value = RedditAuthResult.Success
            } else {
                authResult.value = RedditAuthResult.Error("Failed to retrieve access token")
            }
        }

        return authResult
    }
}
