package io.github.garykam.readit.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.garykam.readit.data.source.remote.RedditApiService
import io.github.garykam.readit.data.source.remote.RedditAuthService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReadItModule {
    @Singleton
    @Provides
    fun provideRedditAuthService(): RedditAuthService {
        return RedditAuthService.create()
    }

    @Singleton
    @Provides
    fun provideRedditApiService(): RedditApiService {
        return RedditApiService.create()
    }
}
