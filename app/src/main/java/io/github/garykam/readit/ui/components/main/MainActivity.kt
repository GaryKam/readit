package io.github.garykam.readit.ui.components.main

import android.os.Bundle
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
import io.github.garykam.readit.ui.theme.ReadItTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ReadItTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = "Username: ${viewModel.name}")
                    Text(text = "Karma: ${viewModel.karma}")
                    Button(onClick = { viewModel.getUser() }) {
                        Text(text = "Get user")
                    }
                }
            }
        }
    }
}
