package io.github.garykam.readit.ui.component.subreddit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import io.github.garykam.readit.data.model.SubredditPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubredditScreen(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubredditViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val subreddits by viewModel.subscribedSubreddits.collectAsState()
    val subredditPosts by viewModel.subredditPosts.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.apply { if (isClosed) open() else close() }
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        AsyncImage(
                            model = user?.avatar,
                            contentDescription = "profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ItemDrawer(
            items = subreddits.map { it.data.prefixedName }.toImmutableList(),
            selectedItem = viewModel.subreddit,
            drawerState = drawerState,
            onItemClick = {
                viewModel.clickSubreddit(it)
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            SubredditPosts(
                posts = subredditPosts.toImmutableList(),
                onLoadClick = { viewModel.showPosts() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ItemDrawer(
    items: ImmutableList<String>,
    selectedItem: String,
    drawerState: DrawerState,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(IntrinsicSize.Max)) {
                for (item in items) {
                    Button(
                        onClick = { onItemClick(item) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (item == selectedItem) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.background
                            )
                        } else {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    ) {
                        Text(text = item)
                        Spacer(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = false
    ) {
        content()
    }
}

@Composable
private fun SubredditPosts(
    posts: ImmutableList<SubredditPost>,
    onLoadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(posts) { post ->
            Text(text = post.data.title)
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }

        if (posts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = onLoadClick) {
                        Text(text = "Load More")
                    }
                }
            }
        }
    }
}
