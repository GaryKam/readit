package io.github.garykam.readit.ui.component.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import io.github.garykam.readit.data.model.RedditAuthResult
import io.github.garykam.readit.ui.component.main.MainActivity
import io.github.garykam.readit.ui.theme.ReadItTheme
import io.github.garykam.readit.util.PreferenceUtil

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (PreferenceUtil.isTokenExpired()) {
            splashScreen.setKeepOnScreenCondition { true }
            viewModel.refreshAccessToken().observe(this) { authResult ->
                when (authResult) {
                    is RedditAuthResult.Success -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }

                    is RedditAuthResult.Error -> {
                        Log.d("AuthActivity", authResult.errorMessage)
                        finish()
                    }
                }
            }
            return
        }

        if (PreferenceUtil.isLoggedIn()) {
            splashScreen.setKeepOnScreenCondition { true }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            ReadItTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { viewModel.launchAuthBrowser(this@AuthActivity) }) {
                        Text(text = "Authenticate")
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val error = intent?.data?.getQueryParameter("error")
        val state = intent?.data?.getQueryParameter("state")
        val code = intent?.data?.getQueryParameter("code")

        viewModel.retrieveAccessToken(error, state, code).observe(this) { authResult ->
            when (authResult) {
                is RedditAuthResult.Success -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }

                is RedditAuthResult.Error -> {
                    Log.d("AuthActivity", authResult.errorMessage)
                }
            }
        }
    }
}
