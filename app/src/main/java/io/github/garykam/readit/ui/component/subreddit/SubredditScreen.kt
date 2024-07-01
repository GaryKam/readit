package io.github.garykam.readit.ui.component.subreddit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.garykam.readit.ui.component.common.ItemDrawer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubredditScreen(
    viewModel: SubredditViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val drawerState = DrawerState(initialValue = DrawerValue.Closed)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.apply { if (isClosed) open() else close() }
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        ItemDrawer(
            modifier = Modifier.padding(innerPadding),
            items = viewModel.subscribedSubreddits.map { it.data.prefixedName },
            selectedItem = "",
            drawerState = drawerState,
            onItemClick = {
                scope.launch {
                    drawerState.close()
                }
            }
        ) {}
    }
}
