package io.github.garykam.readit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.AndroidEntryPoint
import io.github.garykam.readit.theme.ReadItTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        viewModel.launchAuthBrowser(this)

        setContent {
            ReadItTheme {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Cyan)) {}
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val error = intent?.data?.getQueryParameter("error") ?: ""
        val state = intent?.data?.getQueryParameter("state") ?: ""
        val code = intent?.data?.getQueryParameter("code") ?: ""

        if (error.isEmpty() && state.isNotEmpty() && code.isNotEmpty()) {
            viewModel.getAccessToken(code, state)
        } else {
            Log.d("MainActivity", "Failed to authenticate: $error")
        }
    }
}
