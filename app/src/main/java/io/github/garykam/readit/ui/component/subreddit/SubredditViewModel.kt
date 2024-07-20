package io.github.garykam.readit.ui.component.subreddit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.model.RedditUser
import io.github.garykam.readit.data.model.Subreddit
import io.github.garykam.readit.data.model.RedditPost
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
    private val _redditPosts = MutableStateFlow<List<RedditPost>>(emptyList())
    val subredditPosts = _redditPosts.asStateFlow()
    var subreddit by mutableStateOf("")
        private set
    private var after: String? = null

    init {
        viewModelScope.launch {
            _user.update { repository.getUser() }
            repository.getSubscribedSubreddits()?.data?.children?.let { subreddits ->
                _subscribedSubreddits.update { subreddits }
            }
        }
    }

    fun clickSubreddit(subreddit: String) {
        if (this.subreddit == subreddit) {
            return
        }

        this.subreddit = subreddit
        _redditPosts.update { emptyList() }
        after = null

        showPosts()
    }

    fun showPosts() {
        viewModelScope.launch {
            val subredditPosts = if (subreddit.startsWith("r/")) {
                repository.getPostsFromSubreddit(subreddit.removePrefix("r/"), after = after)
            } else if (subreddit.startsWith("u/")) {
                repository.getPostsFromUserProfile(subreddit.removePrefix("u/"), after = after)
            } else {
                null
            }

            subredditPosts?.data?.let { data ->
                _redditPosts.update { it + data.children }
                after = data.after
            }
        }
    }
}
