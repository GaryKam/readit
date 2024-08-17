package io.github.garykam.readit.ui.component.redditpost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.garykam.readit.data.model.RedditListing
import io.github.garykam.readit.data.model.RedditPostComment
import io.github.garykam.readit.ui.component.common.HtmlText
import io.github.garykam.readit.ui.component.main.AppBarState
import io.github.garykam.readit.util.toElapsed

@Composable
fun RedditPostScreen(
    subreddit: String,
    postId: String,
    onAppBarStateUpdate: (AppBarState) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RedditPostViewModel = hiltViewModel()
) {
    val comments by viewModel.comments.collectAsState()

    LaunchedEffect(key1 = true) {
        onAppBarStateUpdate(
            AppBarState(
                title = {},
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

        viewModel.showComments(subreddit, postId)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main content of the reddit post
        comments.firstOrNull()?.data?.let {
            item {
                Surface(shadowElevation = 8.dp) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Row {
                            Text(
                                text = "u/${it.author} • ${it.created.toElapsed()}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Text(
                            text = it.title,
                            modifier = Modifier.padding(bottom = 20.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        it.header?.let {
                            HtmlText(
                                text = it,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(
                            modifier = Modifier.offset(y = -(15.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ThumbUp,
                                contentDescription = null,
                                modifier = Modifier.scale(0.7F)
                            )
                            Text(
                                text = it.score.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        val startPadding = 16.dp
        items(
            items = comments.subList(0, comments.size),
            key = { it.data.id }
        ) { comment ->
            // Individual reply to the main content
            if (comment.data.text != null) {
                Comment(
                    data = comment.data,
                    modifier = Modifier.padding(start = startPadding, top = 2.dp, end = 16.dp, bottom = 2.dp)
                )

                // Nested replies to the individual reply
                comment.data.replies?.let {
                    CommentReplies(
                        replies = it,
                        padding = startPadding + 8.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun Comment(
    data: RedditPostComment.Data,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row {
            Text(
                text = "u/${data.author} • ${data.created.toElapsed()}",
                style = MaterialTheme.typography.labelSmall
            )
        }
        HtmlText(
            text = data.text ?: "",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.offset(y = -(15.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
    }
}

@Composable
private fun CommentReplies(
    replies: RedditListing<RedditPostComment>,
    padding: Dp
) {
    Column {
        for (reply in replies.data.children) {
            Comment(
                data = reply.data,
                modifier = Modifier.padding(start = padding, top = 2.dp, end = 16.dp, bottom = 2.dp)
            )

            reply.data.replies?.let {
                CommentReplies(
                    replies = it,
                    padding = padding + 8.dp
                )
            }
        }
    }
}
