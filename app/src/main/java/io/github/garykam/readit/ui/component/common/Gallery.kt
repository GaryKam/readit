package io.github.garykam.readit.ui.component.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        items(images) {
            AsyncImage(
                model = it,
                contentDescription = "gallery image"
            )
        }
    }
}