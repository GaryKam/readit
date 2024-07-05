package io.github.garykam.readit.ui.component.subreddit

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.model.RedditUser
import io.github.garykam.readit.data.model.Subreddit
import io.github.garykam.readit.data.model.SubredditPost
import io.github.garykam.readit.data.repository.RedditApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubredditViewModel @Inject constructor(
    private val repository: RedditApiRepository
) : ViewModel() {
    private val _user = MutableStateFlow<RedditUser?>(null)
    val user = _user.asStateFlow()
    private val _subscribedSubreddits = MutableStateFlow<List<Subreddit>>(emptyList())
    val subscribedSubreddits = _subscribedSubreddits.asStateFlow()
    private val _subredditPosts = MutableStateFlow<List<SubredditPost>>(emptyList())
    val subredditPosts = _subredditPosts.asStateFlow()

    var subreddit by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            _user.update { repository.getUser() }
            repository.getSubscribedSubredditsListing()?.data?.children?.let { subreddits ->
                _subscribedSubreddits.update { subreddits }
            }
        }
    }

    fun clickSubreddit(subreddit: String) {
        this.subreddit = subreddit
        viewModelScope.launch {
            if (subreddit.startsWith("r/")) {
                repository.getSubredditPosts(subreddit.removePrefix("r/"))?.data?.children?.let { posts ->
                    _subredditPosts.update { posts }
                }
            } else if (subreddit.startsWith("u/")) {
                repository.getUserSubreddit(subreddit.removePrefix("u/"))?.data?.children?.let { posts ->
                    _subredditPosts.update { posts }
                }
            }
        }
    }
}
