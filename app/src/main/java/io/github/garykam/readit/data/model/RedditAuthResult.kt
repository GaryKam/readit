package io.github.garykam.readit.data.model

sealed class RedditAuthResult {
    data object Success : RedditAuthResult()
    class Error(val errorMessage: String) : RedditAuthResult()
}
