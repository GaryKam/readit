package io.github.garykam.readit

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.garykam.readit.util.PreferenceUtil

@HiltAndroidApp
class ReadItApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceUtil.init(this)
    }
}
