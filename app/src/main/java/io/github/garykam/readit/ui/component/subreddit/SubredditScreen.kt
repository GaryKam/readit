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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.garykam.readit.R
import io.github.garykam.readit.data.model.RedditPost
import io.github.garykam.readit.ui.component.common.DropdownButton
import io.github.garykam.readit.ui.component.common.Gallery
import io.github.garykam.readit.ui.component.common.HtmlText
import io.github.garykam.readit.ui.component.common.ItemDrawer
import io.github.garykam.readit.ui.component.common.MediaPlayer
import io.github.garykam.readit.ui.component.common.Pill
import io.github.garykam.readit.ui.component.common.SearchBar
import io.github.garykam.readit.ui.component.main.AppBarState
import io.github.garykam.readit.util.toElapsed
import io.github.garykam.readit.util.toShortened
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
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
    val localSubscribedSubreddits by viewModel.localSubscribedSubreddits.collectAsState()
    val redditPosts by viewModel.redditPosts.collectAsState()
    val activeSubreddit by viewModel.activeSubreddit.collectAsState()
    val subredditSearch by viewModel.subredditSearch.collectAsState()
    val postOrder by viewModel.postOrder.collectAsState()
    val topPostOrder by viewModel.topPostOrder.collectAsState()
    val subredditToSubscribe by viewModel.subredditToSubscribe.collectAsState()

    LaunchedEffect(key1 = true) {
        onAppBarStateUpdate(
            AppBarState(
                title = {
                    SearchBar(
                        query = subredditSearch,
                        onQueryChange = { viewModel.changeSearch(it) },
                        onSearch = {
                            focusManager.clearFocus()
                            viewModel.searchSubreddit(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused) {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    viewModel.changeSearch("")
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "clear search"
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
                            focusManager.clearFocus()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "menu"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onNavigateToProfile()
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    ) {
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
        items = subscribedSubreddits.toImmutableList(),
        selectedItem = activeSubreddit,
        drawerState = drawerState,
        onItemClick = {
            viewModel.selectSubreddit(it)
            scope.launch { drawerState.close() }
        },
        onItemLongClick = { viewModel.promptSubscription(it) },
        modifier = modifier
    ) {
        PostOrderButtons(
            orderItems = viewModel.orderList.toImmutableList(),
            topOrderItems = viewModel.topOrderMap.keys.toImmutableList(),
            selectedOrder = postOrder,
            selectedTopOrder = topPostOrder,
            onOrderClick = { viewModel.orderPosts(it) },
            onTopOrderClick = { viewModel.orderPosts(postOrder, it) },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        RedditPosts(
            canLoadMore = viewModel.canLoadMore,
            posts = redditPosts.toImmutableList(),
            onPostClick = { onNavigateToRedditPost(activeSubreddit, it) },
            onLoadClick = { viewModel.loadPosts() },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
    }

    SubscribeDialog(
        subreddit = subredditToSubscribe,
        isAlreadySubscribed = subscribedSubreddits.contains(subredditToSubscribe) && !localSubscribedSubreddits.contains(subredditToSubscribe),
        onDismiss = { viewModel.promptSubscription("") },
        onConfirm = { viewModel.confirmSubscription(it) }
    )
}

@Composable
private fun PostOrderButtons(
    orderItems: ImmutableList<String>,
    topOrderItems: ImmutableList<String>,
    selectedOrder: String,
    selectedTopOrder: String,
    onOrderClick: (String) -> Unit,
    onTopOrderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        DropdownButton(
            items = orderItems,
            selectedItem = selectedOrder,
            onItemClick = { onOrderClick(it) },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
        )
        if (selectedOrder == orderItems[2]) {
            DropdownButton(
                items = topOrderItems,
                selectedItem = selectedTopOrder,
                onItemClick = { onTopOrderClick(it) },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}

@Composable
private fun RedditPosts(
    canLoadMore: Boolean,
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
                post = post.data,
                onPostClick = onPostClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (canLoadMore) {
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
    post: RedditPost.Data,
    onPostClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .clickable { onPostClick(post.id) }
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "u/${post.author} • ${post.created.toElapsed()}",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = post.title.removeSuffix("\n"),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
            when {
                post.hasImage -> {
                    AsyncImage(
                        model = post.url,
                        contentDescription = "post image",
                        modifier = Modifier
                            .sizeIn(maxHeight = 300.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 4.dp)
                            .clickable(false) {}
                    )
                }

                post.hasGallery -> {
                    Gallery(
                        galleryData = post.galleryData!!,
                        mediaMetadata = post.mediaMetadata!!.toImmutableMap(),
                        modifier = Modifier.requiredHeight(300.dp)
                    )
                }

                post.hasThumbnail -> {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append(post.url)
                                }
                                addLink(LinkAnnotation.Url(post.url!!), 0, post.url.length)
                            },
                            modifier = Modifier.weight(0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(post.thumbnail)
                                .crossfade(true)
                                .build(),
                            contentDescription = "thumbnail image",
                            modifier = Modifier
                                .weight(0.2f)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                post.hasVideo -> {
                    MediaPlayer(
                        url = post.videoData!!.data.dashUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .sizeIn(maxHeight = 300.dp)
                    )
                }

                post.hasLink -> {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append(post.url)
                            }
                            addLink(LinkAnnotation.Url(post.url!!), 0, post.url.length)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                post.text != null -> {
                    HtmlText(
                        text = post.text,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
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
                        text = post.score.toShortened(),
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
                        text = post.comments.toShortened(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscribeDialog(
    subreddit: String,
    isAlreadySubscribed: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit
) {
    if (subreddit.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(isAlreadySubscribed)
                        onDismiss()
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Dismiss")
                }
            },
            text = {
                Text(
                    text = if (isAlreadySubscribed) {
                        "Unsubscribe from $subreddit?"
                    } else {
                        "Subscribe to $subreddit?"
                    }
                )
            }
        )
    }
}
