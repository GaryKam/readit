package io.github.garykam.readit.ui.component.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.model.RedditUser
import io.github.garykam.readit.data.repository.RedditApiRepository
import io.github.garykam.readit.util.PreferenceUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: RedditApiRepository
) : ViewModel() {
    private val _user = MutableStateFlow<RedditUser?>(null)
    val user = _user.asStateFlow()

    fun logOut() {
        PreferenceUtil.logOut()
    }

    init {
        viewModelScope.launch {
            _user.update { repository.getUser() }
        }
    }
}
