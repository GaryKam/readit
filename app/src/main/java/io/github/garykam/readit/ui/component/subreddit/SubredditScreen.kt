package io.github.garykam.readit.ui.component.subreddit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import io.github.garykam.readit.R
import io.github.garykam.readit.data.model.RedditPost
import io.github.garykam.readit.ui.component.common.HtmlText
import io.github.garykam.readit.ui.component.common.Pill
import io.github.garykam.readit.ui.component.common.SearchBar
import io.github.garykam.readit.ui.component.main.AppBarState
import io.github.garykam.readit.util.toElapsed
import io.github.garykam.readit.util.toShortened
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun SubredditScreen(
    onAppBarStateUpdate: (AppBarState) -> Unit,
    onNavigateToRedditPost: (String, String) -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubredditViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val user by viewModel.user.collectAsState()
    val subscribedSubreddits by viewModel.subscribedSubreddits.collectAsState()
    val redditPosts by viewModel.redditPosts.collectAsState()
    val activeSubreddit by viewModel.activeSubreddit.collectAsState()
    val subredditSearch by viewModel.subredditSearch.collectAsState()

    LaunchedEffect(key1 = true) {
        onAppBarStateUpdate(
            AppBarState(
                title = {
                    SearchBar(
                        query = subredditSearch,
                        onQueryChange = { viewModel.changeSearch(it) },
                        onSearch = {
                            focusManager.clearFocus()
                            viewModel.selectSubreddit(it)
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                focusManager.clearFocus()
                                viewModel.changeSearch(activeSubreddit)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "cancel search"
                                )
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.apply { if (isClosed) open() else close() }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
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
        )
    }

    ItemDrawer(
        items = subscribedSubreddits.map { it.data.prefixedName }.toImmutableList(),
        selectedItem = activeSubreddit,
        drawerState = drawerState,
        onItemClick = {
            viewModel.selectSubreddit(it)
            scope.launch { drawerState.close() }
        },
        modifier = modifier
    ) {
        RedditPosts(
            posts = redditPosts.toImmutableList(),
            onPostClick = { postId -> onNavigateToRedditPost(activeSubreddit, postId) },
            onLoadClick = { viewModel.loadPosts() },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
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
                        Text(
                            text = item,
                            modifier = Modifier.fillMaxWidth()
                        )
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
private fun RedditPosts(
    posts: ImmutableList<RedditPost>,
    onPostClick: (String) -> Unit,
    onLoadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = posts,
            key = { it.data.id }
        ) { post ->
            RedditPost(
                data = post.data,
                onPostClick = onPostClick,
                modifier = Modifier.fillMaxWidth()
            )
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

@Composable
private fun RedditPost(
    data: RedditPost.Data,
    onPostClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .clickable { onPostClick(data.id) }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "u/${data.author} • ${data.created.toElapsed()}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = data.title.removeSuffix("\n"),
                modifier = Modifier.padding(vertical = 4.dp),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
            if (!data.text.isNullOrEmpty()) {
                HtmlText(
                    text = data.text,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 4,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Max)
            ) {
                Pill(modifier = Modifier.fillMaxHeight()) {
                    Icon(
                        imageVector = Icons.Outlined.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.scale(0.7F)
                    )
                    Text(
                        text = data.score.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(10.dp)
                )
                Pill(modifier = Modifier.fillMaxHeight()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chatbox),
                        contentDescription = null,
                        modifier = Modifier.scale(0.8F)
                    )
                    Text(
                        text = data.comments.toShortened(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
