package io.github.garykam.readit.ui.component.subreddit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.model.RedditPost
import io.github.garykam.readit.data.model.RedditUser
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
    private val _subscribedSubreddits = MutableStateFlow<List<String>>(emptyList())
    private val _redditPosts = MutableStateFlow<List<RedditPost>>(emptyList())
    private val _activeSubreddit = MutableStateFlow("")
    private val _subredditSearch = MutableStateFlow("")
    private val _postOrder = MutableStateFlow("")
    private val _topPostOrder = MutableStateFlow("")
    private var _after = MutableStateFlow<String?>(null)
    private var _subredditToSubscribe = MutableStateFlow("")
    val user = _user.asStateFlow()
    val subscribedSubreddits = _subscribedSubreddits.asStateFlow()
    val redditPosts = _redditPosts.asStateFlow()
    val activeSubreddit = _activeSubreddit.asStateFlow()
    val subredditSearch = _subredditSearch.asStateFlow()
    val subredditToSubscribe = _subredditToSubscribe.asStateFlow()
    val postOrder = _postOrder.asStateFlow()
    val topPostOrder = _topPostOrder.asStateFlow()
    val orderList = listOf("HOT", "NEW", "TOP", "RISING")
    val topOrderMap = mapOf(
        "NOW" to "hour", "TODAY" to "day", "THIS WEEK" to "week",
        "THIS MONTH" to "month", "THIS YEAR" to "year", "ALL TIME" to "all"
    )
    val canLoadMore: Boolean
        get() = _after.value != null

    init {
        viewModelScope.launch {
            val activeSubreddit = PreferenceUtil.getSubreddit()
            selectSubreddit(activeSubreddit)

            _user.update { repository.getUser() }

            val favoriteSubreddits = PreferenceUtil.getFavoriteSubreddits().toList()
            _subscribedSubreddits.update { favoriteSubreddits }
            var isAlreadySubscribed = false
            for (subreddit in favoriteSubreddits) {
                if (subreddit == activeSubreddit) {
                    isAlreadySubscribed = true
                    break
                }
            }
            if (!isAlreadySubscribed) {
                _subscribedSubreddits.update { it + activeSubreddit }
            }
        }
    }

    fun changeSearch(subreddit: String) {
        _subredditSearch.update { subreddit }
    }

    fun searchSubreddit(query: String) {
        when {
            query.startsWith(SUBREDDIT_PREFIX) || query.startsWith(USER_PROFILE_PREFIX) -> {
                _subscribedSubreddits.update { it + query }
                selectSubreddit(query)
            }

            else -> viewModelScope.launch {
                _redditPosts.update { emptyList() }
                _subredditSearch.update { query }
                _after.update { null }

                loadPosts()
            }
        }
    }

    fun selectSubreddit(subreddit: String) {
        if (subreddit.isEmpty()) {
            return
        }

        _redditPosts.update { emptyList() }
        _activeSubreddit.update { subreddit }
        _subredditSearch.update { subreddit }
        _postOrder.update { PreferenceUtil.getPostOrder(subreddit).ifEmpty { orderList[0] } }
        _topPostOrder.update { topOrderMap.keys.elementAt(1) }
        _after.update { null }

        loadPosts()
        PreferenceUtil.setSubreddit(subreddit)
    }

    fun loadPosts() {

        viewModelScope.launch {
            val subreddit = _activeSubreddit.value
            val subredditPosts = when {
                !_subredditSearch.value.startsWith(SUBREDDIT_PREFIX) && !_subredditSearch.value.startsWith(USER_PROFILE_PREFIX) -> {
                    repository.getPostsFromSearch(
                        subreddit.removePrefix(SUBREDDIT_PREFIX).removePrefix(USER_PROFILE_PREFIX),
                        _postOrder.value.lowercase(),
                        if (_postOrder.value == orderList[2]) topOrderMap[_topPostOrder.value] else null,
                        _after.value,
                        _subredditSearch.value
                    )
                }

                subreddit.startsWith(SUBREDDIT_PREFIX) ->
                    repository.getPostsFromSubreddit(
                        subreddit.removePrefix(SUBREDDIT_PREFIX),
                        _postOrder.value.lowercase(),
                        if (_postOrder.value == orderList[2]) topOrderMap[_topPostOrder.value] else null,
                        _after.value
                    )

                subreddit.startsWith(USER_PROFILE_PREFIX) ->
                    repository.getPostsFromUserProfile(
                        subreddit.removePrefix(USER_PROFILE_PREFIX),
                        after = _after.value
                    )

                else -> null
            }


            subredditPosts?.data?.let { data ->
                _redditPosts.update { it + data.children }
                _after.update { data.after }
            }
        }
    }

    fun orderPosts(
        order: String,
        topPostOrder: String = topOrderMap.keys.elementAt(1)
    ) {
        if (_activeSubreddit.value.isNotEmpty()) {
            _redditPosts.update { emptyList() }
            _postOrder.update { order }
            _topPostOrder.update { topPostOrder }
            _after.update { null }

            loadPosts()
            PreferenceUtil.setPostOrder(_activeSubreddit.value, order)
        }
    }

    fun promptSubscribe(subreddit: String) {
        _subredditToSubscribe.update { subreddit }
    }

    fun confirmSubscribe() {

    }

    companion object {
        private const val SUBREDDIT_PREFIX = "r/"
        private const val USER_PROFILE_PREFIX = "u/"
    }
}
