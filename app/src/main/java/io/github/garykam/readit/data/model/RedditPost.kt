package io.github.garykam.readit.data.model

import com.google.gson.annotations.SerializedName

data class RedditPost(
    val data: Data
) {
    data class Data(
        @SerializedName("name")
        val id: String,
        val author: String,
        val title: String,
        @SerializedName("selftext_html")
        val text: String?,
        val url: String?,
        val score: Int,
        val created: Long,
        @SerializedName("num_comments")
        val comments: Int,
        @SerializedName("gallery_data")
        val galleryData: GalleryData?,
        @SerializedName("media_metadata")
        val mediaMetadata: Map<String, MediaMetadata>?,
        val thumbnail: String?
    )

    val hasImage: Boolean
        get() = data.url?.endsWith(".jpeg") ?: false

    val hasGallery: Boolean
        get() = data.galleryData != null && data.mediaMetadata != null

    val hasThumbnail: Boolean
        get() = data.thumbnail?.endsWith(".jpg") ?: false

    val isLink: Boolean
        get() = data.text.isNullOrEmpty() && !data.url.isNullOrEmpty()
}

data class GalleryData(
    val items: List<Data>
) {
    data class Data(
        @SerializedName("media_id")
        val mediaId: String
    )
}

data class MediaMetadata(
    val status: String,
    @SerializedName("s")
    val data: Data
) {
    data class Data(
        val x: Int,
        val y: Int,
        @SerializedName("u")
        val image: String
    )
}
