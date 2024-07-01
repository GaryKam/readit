package io.github.garykam.readit.ui.component.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.github.garykam.readit.ui.navigation.ReadItNavHost
import io.github.garykam.readit.ui.theme.ReadItTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ReadItTheme {
                ReadItNavHost()
            }
        }
    }
}
