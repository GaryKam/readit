package io.github.garykam.readit.ui.component.redditpost

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
    private val _comments = MutableStateFlow<List<RedditPostComment>>(emptyList())
    val comments = _comments.asStateFlow()

    fun showComments(subreddit: String, postId: String) {
        viewModelScope.launch {
            repository.getCommentsFromId(
                subreddit.removePrefix("r/"),
                postId.removePrefix("t3_")
            )?.forEach { thread ->
                thread.data.children.forEach { comment ->
                    _comments.update { it + comment }
                }
            }
        }
    }
}
