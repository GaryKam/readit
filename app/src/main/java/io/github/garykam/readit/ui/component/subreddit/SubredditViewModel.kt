package io.github.garykam.readit.ui.component.subreddit

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.model.Subreddit
import io.github.garykam.readit.data.repository.RedditApiRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubredditViewModel @Inject constructor(
    private val repository: RedditApiRepository
) : ViewModel() {
    private val _subscribedSubreddits = mutableStateListOf<Subreddit>()
    val subscribedSubreddits: List<Subreddit> = _subscribedSubreddits

    init {
        viewModelScope.launch {
            repository.getSubscribedSubredditsListing()?.data?.children?.let {
                _subscribedSubreddits.addAll(it)
            }
        }
    }
}
