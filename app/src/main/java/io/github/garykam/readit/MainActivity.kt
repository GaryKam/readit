package io.github.garykam.readit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.stringResource
import io.github.garykam.readit.theme.ReadItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ReadItTheme {
                Column {
                    val clientId = "2tTU7W_Ode727mcjbyMgKw"
                    val state = "myRandomState"
                    val redirectUri = "readit://auth"
                    val duration = "permanent"
                    val scope = "identity"
                    val authUrl = Uri.parse(stringResource(R.string.reddit_auth_url, clientId, state, redirectUri, duration, scope))

                    CustomTabsIntent.Builder().build().run {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        launchUrl(applicationContext, authUrl)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val error = intent?.data?.getQueryParameter("error")
        val code = intent?.data?.getQueryParameter("code")
        val state = intent?.data?.getQueryParameter("state")

        Log.d("TokenActivity", "error: $error")
        Log.d("TokenActivity", "code: $code")
        Log.d("TokenActivity", "state: $state")
    }
}
