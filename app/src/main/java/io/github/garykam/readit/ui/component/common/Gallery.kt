package io.github.garykam.readit.ui.component.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.garykam.readit.data.model.GalleryData
import io.github.garykam.readit.data.model.MediaMetadata
import kotlinx.collections.immutable.ImmutableMap

@Composable
fun Gallery(
    galleryData: GalleryData,
    mediaMetadata: ImmutableMap<String, MediaMetadata>,
    modifier: Modifier = Modifier
) {
    val images = mutableListOf<String>()
    for (item in galleryData.items) {
        val image = mediaMetadata[item.mediaId]?.data?.image
        if (image != null) {
            images.add(image)
        }
    }

    LazyRow(
        modifier = modifier.clickable(false) {},
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(images) { index, image ->
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = image,
                    contentDescription = "gallery image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Pill(
                    modifier = Modifier
                        .padding(5.dp)
                        .scale(0.7F),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5F)
                ) {
                    Text(text = "$index/${images.size}")
                }
            }
        }
    }
}
