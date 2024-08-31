package io.github.garykam.readit.ui.component.common

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun MediaPlayer(
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember(url) {
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()

        ExoPlayer.Builder(context)
            .build()
            .apply {
                setMediaItem(mediaItem)
                prepare()
            }
    }

    DisposableEffect(key1 = Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = { PlayerView(it).apply { player = exoPlayer } },
        modifier = modifier,
        onReset = {}
    )
}
