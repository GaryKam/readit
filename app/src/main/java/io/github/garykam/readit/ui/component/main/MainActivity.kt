package io.github.garykam.readit.ui.component.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.garykam.readit.ui.navigation.ReadItNavHost
import io.github.garykam.readit.ui.theme.ReadItTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ReadItTheme(darkTheme = false) {
                ReadItApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReadItApp() {
    var appBarState by remember { mutableStateOf(AppBarState()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = appBarState.title,
                navigationIcon = appBarState.navigationIcon,
                actions = appBarState.actions
            )
        }
    ) { innerPadding ->
        ReadItNavHost(
            onAppBarStateUpdate = { appBarState = it },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
