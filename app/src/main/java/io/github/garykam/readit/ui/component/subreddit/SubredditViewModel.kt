package io.github.garykam.readit.ui.component.subreddit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.model.RedditUser
import io.github.garykam.readit.data.model.Subreddit
import io.github.garykam.readit.data.model.RedditPost
import io.github.garykam.readit.data.repository.RedditApiRepository
import io.github.garykam.readit.util.PreferenceUtil
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
    private val _subscribedSubreddits = MutableStateFlow<List<Subreddit>>(emptyList())
    private val _redditPosts = MutableStateFlow<List<RedditPost>>(emptyList())
    private val _activeSubreddit = MutableStateFlow("")
    private val _subredditSearch = MutableStateFlow("")
    private var after: String? = null
    val user = _user.asStateFlow()
    val subscribedSubreddits = _subscribedSubreddits.asStateFlow()
    val redditPosts = _redditPosts.asStateFlow()
    val activeSubreddit = _activeSubreddit.asStateFlow()
    val subredditSearch = _subredditSearch.asStateFlow()

    init {
        viewModelScope.launch {
            val subreddit = PreferenceUtil.getSubreddit()
            if (subreddit.isNotEmpty()) {
                selectSubreddit(subreddit)
            }

            _activeSubreddit.update { subreddit }
            _subredditSearch.update { subreddit }
            _user.update { repository.getUser() }
            repository.getSubscribedSubreddits()?.data?.children?.let { subreddits ->
                _subscribedSubreddits.update { subreddits }
            }
        }
    }

    fun changeSearch(subreddit: String) {
        _subredditSearch.update { subreddit }
    }

    fun selectSubreddit(subreddit: String) {
        if (activeSubreddit.value == subreddit) {
            return
        }

        _activeSubreddit.update { subreddit }
        _subredditSearch.update { subreddit }
        _redditPosts.update { emptyList() }
        after = null
        PreferenceUtil.setSubreddit(subreddit)

        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            val subreddit = activeSubreddit.value
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
