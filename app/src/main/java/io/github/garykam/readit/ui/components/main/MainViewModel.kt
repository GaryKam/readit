package io.github.garykam.readit.ui.components.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.readit.data.RedditApiRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiRepository: RedditApiRepository
) : ViewModel() {
    var name by mutableStateOf("")
        private set
    var karma by mutableIntStateOf(0)
        private set

    fun getUser() {
        viewModelScope.launch {
            val user = apiRepository.getUser()
            name = user?.name ?: "null"
            karma = user?.karma?.toInt() ?: 0
        }
    }
}
