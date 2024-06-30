package io.github.garykam.readit.ui.components.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.garykam.readit.data.model.RedditAuthResult
import io.github.garykam.readit.ui.components.main.MainActivity
import io.github.garykam.readit.ui.theme.ReadItTheme
import io.github.garykam.readit.util.PreferenceUtil

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (PreferenceUtil.isTokenExpired()) {
            viewModel.refreshAccessToken()
            startActivity(Intent(this, MainActivity::class.java))
        }

        setContent {
            ReadItTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Button(onClick = { viewModel.launchAuthBrowser(applicationContext) }) {
                        Text(text = "Authenticate")
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val error = intent?.data?.getQueryParameter("error") ?: ""
        val state = intent?.data?.getQueryParameter("state") ?: ""
        val code = intent?.data?.getQueryParameter("code") ?: ""

        if (error.isEmpty() && state.isNotEmpty() && code.isNotEmpty()) {
            viewModel.retrieveAccessToken(code, state).observe(this) { authResult ->
                when (authResult) {
                    is RedditAuthResult.Success -> {
                        startActivity(Intent(this, MainActivity::class.java))
                    }

                    is RedditAuthResult.Error -> {
                        Log.d("AuthActivity", authResult.errorMessage)
                    }
                }
            }
        } else {
            Log.d("AuthActivity", "Failed to authenticate: $error")
        }
    }
}
