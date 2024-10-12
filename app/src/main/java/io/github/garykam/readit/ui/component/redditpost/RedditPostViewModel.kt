package io.github.garykam.readit.ui.component.redditpost

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.model.RedditPostComment
import io.github.garykam.readit.data.repository.RedditRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedditPostViewModel @Inject constructor(
    private val repository: RedditRepository
) : ViewModel() {
    val sortMap = mapOf(
        "BEST" to "confidence", "TOP" to "top", "NEW" to "new",
        "CONTROVERSIAL" to "controversial", "OLD" to "old", "Q&A" to "qa"
    )
    private val _content = MutableStateFlow<RedditPostComment?>(null)
    private val _comments = MutableStateFlow<List<RedditPostComment>>(emptyList())
    private val _commentSort = MutableStateFlow(sortMap.keys.elementAt(0))
    val content = _content.asStateFlow()
    val comments = _comments.asStateFlow()
    val commentSort = _commentSort.asStateFlow()

    fun loadComments(
        subreddit: String,
        postId: String
    ) {
        viewModelScope.launch {
            Log.d("g", "$subreddit $postId")
            val commentThreads = repository.getCommentsFromId(
                subreddit.removePrefix("r/"),
                postId.removePrefix("t3_"),
                _commentSort.value
            )

            if (_content.value == null) {
                _content.update { commentThreads?.firstOrNull()?.data?.children?.firstOrNull() }
            }

            commentThreads?.subList(1, commentThreads.size)?.forEach { thread ->
                thread.data.children.forEach { comment ->
                    _comments.update { it + comment }
                }
            }
        }
    }

    fun sortComments(
        subreddit: String,
        postId: String,
        sortBy: String
    ) {
        _comments.update { emptyList() }
        _commentSort.update { sortBy }
        loadComments(subreddit, postId)
    }
}
