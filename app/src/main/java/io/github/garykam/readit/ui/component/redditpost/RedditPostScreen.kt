package io.github.garykam.readit.ui.component.redditpost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.garykam.readit.R
import io.github.garykam.readit.data.model.RedditListing
import io.github.garykam.readit.data.model.RedditPostComment
import io.github.garykam.readit.ui.component.common.DropdownButton
import io.github.garykam.readit.ui.component.common.Gallery
import io.github.garykam.readit.ui.component.common.HtmlText
import io.github.garykam.readit.ui.component.common.MediaPlayer
import io.github.garykam.readit.ui.component.common.Pill
import io.github.garykam.readit.ui.component.main.AppBarState
import io.github.garykam.readit.util.toElapsed
import io.github.garykam.readit.util.toShortened
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

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
    val commentSort by viewModel.commentSort.collectAsState()
    val content by viewModel.content.collectAsState()

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

        viewModel.loadComments(subreddit, postId)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main content of the reddit post
        content?.data?.let {
            item {
                Content(post = it)
            }
        }

        // Comment sort button
        item {
            DropdownButton(
                items = viewModel.sortMap.keys.toImmutableList(),
                selectedItem = commentSort,
                onItemClick = { viewModel.sortComments(subreddit, postId, it) },
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
            )
        }

        val startPadding = 16.dp
        items(
            items = comments.subList(0, comments.size),
            key = { it.data.id }
        ) {
            val comment = it.data
            // Individual reply to the main content
            if (comment.text != null && comment.author != "[deleted]") {
                Comment(
                    comment = comment,
                    modifier = Modifier.padding(start = startPadding, top = 2.dp, end = 16.dp, bottom = 2.dp)
                )

                // Nested replies to the individual reply
                comment.replies?.let { replies ->
                    CommentReplies(
                        replies = replies,
                        padding = startPadding + 8.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(post: RedditPostComment.Data) {
    Surface(shadowElevation = 8.dp) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    text = "u/${post.author} • ${post.created.toElapsed()}",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                text = post.title,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
            post.header?.let {
                HtmlText(
                    text = it,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            when {
                post.hasImage -> {
                    AsyncImage(
                        model = post.url,
                        contentDescription = "post image",
                        modifier = Modifier
                            .sizeIn(maxHeight = 500.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 4.dp)
                    )
                }

                post.hasGallery -> {
                    Gallery(
                        galleryData = post.galleryData!!,
                        mediaMetadata = post.mediaMetadata!!.toImmutableMap(),
                        modifier = Modifier
                            .heightIn(max = 500.dp)
                            .padding(bottom = 4.dp)
                    )
                }

                post.hasThumbnail -> {
                    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(post.thumbnail)
                                .crossfade(true)
                                .build(),
                            contentDescription = "thumbnail image",
                            modifier = Modifier
                                .sizeIn(minWidth = 300.dp, minHeight = 300.dp)
                                .align(Alignment.CenterHorizontally)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append(post.domain)
                                }
                                addLink(LinkAnnotation.Url(post.url!!), 0, post.domain!!.length)
                            },
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                post.hasVideo -> {
                    MediaPlayer(
                        url = post.videoData!!.data.dashUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .sizeIn(maxHeight = 300.dp)
                            .padding(bottom = 4.dp)
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
                        text = post.score.toString(),
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
private fun Comment(
    comment: RedditPostComment.Data,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row {
            Text(
                text = "u/${comment.author} • ${comment.created.toElapsed()}",
                style = MaterialTheme.typography.labelSmall
            )
        }
        comment.text?.let {
            HtmlText(
                text = it,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall
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
                text = comment.score.toString(),
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
            if (reply.data.author == null) {
                continue
            }

            Comment(
                comment = reply.data,
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
