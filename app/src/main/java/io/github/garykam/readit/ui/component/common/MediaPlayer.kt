package io.github.garykam.readit.ui.component.common

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.AndroidEmbeddedExternalSurface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import io.github.garykam.readit.R

@OptIn(UnstableApi::class)
@Composable
fun MediaPlayer(
    url: String,
    modifier: Modifier = Modifier
) {
    val mediaItem = remember {
        MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
    }
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            prepare()
        }
    }
    var isPlaying by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.clickable {
            isPlaying = !isPlaying
            if (isPlaying) exoPlayer.play() else exoPlayer.pause()
        },
        contentAlignment = Alignment.Center
    ) {
        AndroidEmbeddedExternalSurface {
            onSurface { surface, _, _ ->
                exoPlayer.setVideoSurface(surface)
                surface.onDestroyed {
                    exoPlayer.setVideoSurface(null)
                    exoPlayer.release()
                }
            }
        }

        AnimatedVisibility(visible = !isPlaying) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "play media",
                modifier = Modifier
                    .scale(2F)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7F),
                        shape = CircleShape
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            var isMuted by remember { mutableStateOf(false) }
            IconButton(
                onClick = {
                    isMuted = !isMuted
                    exoPlayer.volume = if (isMuted) 0F else 1F
                }
            ) {
                Icon(
                    painter = painterResource(if (isMuted) R.drawable.ic_volume_mute else R.drawable.ic_volume),
                    contentDescription = "volume",
                    modifier = Modifier.size(20.dp)

                )
            }
        }
    }
}
