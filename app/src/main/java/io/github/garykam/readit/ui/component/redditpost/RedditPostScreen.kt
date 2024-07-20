package io.github.garykam.readit.ui.component.redditpost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.garykam.readit.ui.component.common.HtmlText
import io.github.garykam.readit.ui.component.main.AppBarState

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
        comments.firstOrNull()?.data?.let {
            item {
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
                    }
                }
            }
        }

        items(items = comments.subList(0, comments.size)) { comment ->
            comment.data.text?.let {
                HtmlText(
                    text = it,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
