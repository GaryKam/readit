package io.github.garykam.readit.ui.component.subreddit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.model.RedditPost
import io.github.garykam.readit.data.model.RedditUser
import io.github.garykam.readit.data.model.Subreddit
import io.github.garykam.readit.data.repository.RedditRepository
import io.github.garykam.readit.util.PreferenceUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubredditViewModel @Inject constructor(
    private val repository: RedditRepository
) : ViewModel() {
    private val _user = MutableStateFlow<RedditUser?>(null)
    private val _subscribedSubreddits = MutableStateFlow<List<Subreddit>>(emptyList())
    private val _redditPosts = MutableStateFlow<List<RedditPost>>(emptyList())
    private val _activeSubreddit = MutableStateFlow("")
    private val _subredditSearch = MutableStateFlow("")
    private val _postOrder = MutableStateFlow("")
    private var _after = MutableStateFlow<String?>(null)
    val user = _user.asStateFlow()
    val subscribedSubreddits = _subscribedSubreddits.asStateFlow()
    val redditPosts = _redditPosts.asStateFlow()
    val activeSubreddit = _activeSubreddit.asStateFlow()
    val subredditSearch = _subredditSearch.asStateFlow()
    val postOrder = _postOrder.asStateFlow()
    val after = _after.asStateFlow()
    val orderList = listOf("HOT", "NEW", "TOP", "RISING")

    init {
        viewModelScope.launch {
            selectSubreddit(PreferenceUtil.getSubreddit())
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
        if (_activeSubreddit.value == subreddit || subreddit.isEmpty()) {
            return
        }

        _redditPosts.update { emptyList() }
        _activeSubreddit.update { subreddit }
        _subredditSearch.update { subreddit }
        _postOrder.update { PreferenceUtil.getPostOrder(subreddit).ifEmpty { orderList[0] } }
        _after.update { null }

        loadPosts()
        PreferenceUtil.setSubreddit(subreddit)
    }

    fun orderPosts(order: String) {
        if (_activeSubreddit.value.isNotEmpty()) {
            _redditPosts.update { emptyList() }
            _postOrder.update { order }
            _after.update { null }

            loadPosts()
            PreferenceUtil.setPostOrder(_activeSubreddit.value.lowercase(), order)
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            val subreddit = _activeSubreddit.value
            val subredditPosts = if (subreddit.startsWith("r/")) {
                repository.getPostsFromSubreddit(subreddit.removePrefix("r/"), _postOrder.value.lowercase(), _after.value)
            } else if (subreddit.startsWith("u/")) {
                repository.getPostsFromUserProfile(subreddit.removePrefix("u/"), after = _after.value)
            } else {
                null
            }

            subredditPosts?.data?.let { data ->
                _redditPosts.update { it + data.children }
                _after.update { data.after }
            }
        }
    }
}
