package io.github.garykam.readit.ui.component.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import io.github.garykam.readit.R
import io.github.garykam.readit.ui.component.main.AppBarState
import io.github.garykam.readit.util.PreferenceUtil

@Composable
fun ProfileScreen(
    onAppBarStateUpdate: (AppBarState) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    onThemeChange: (Boolean) -> Unit
) {
    val user by viewModel.user.collectAsState()

    LaunchedEffect(key1 = true) {
        onAppBarStateUpdate(
            AppBarState(
                title = { Text(text = "Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            )
        )
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        AsyncImage(
            model = user?.avatar,
            contentDescription = null
        )
        Text(text = user?.name.orEmpty())
        Text(text = "${(user?.karma ?: 0)} Karma")
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Dark Theme", modifier = Modifier.padding(end = 20.dp))
            var darkTheme by remember { mutableStateOf(PreferenceUtil.isDarkTheme()) }
            Switch(
                checked = darkTheme,
                onCheckedChange = {
                    darkTheme = it
                    onThemeChange(it)
                    PreferenceUtil.setDarkTheme(it)
                }
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
        Button(
            onClick = {
                onNavigateToAuth()
                viewModel.logOut()
            },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
        ) {
            Text(text = "Log Out")
        }
    }
}
