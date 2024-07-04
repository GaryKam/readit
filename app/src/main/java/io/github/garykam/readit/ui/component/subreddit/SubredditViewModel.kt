package io.github.garykam.readit.ui.component.subreddit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.model.Subreddit
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

    private val _subscribedSubreddits = MutableStateFlow<List<Subreddit>>(emptyList())
    val subscribedSubreddits = _subscribedSubreddits.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSubscribedSubredditsListing()?.data?.children?.let { subreddits ->
                _subscribedSubreddits.update { subreddits }
            }
        }
    }
}
