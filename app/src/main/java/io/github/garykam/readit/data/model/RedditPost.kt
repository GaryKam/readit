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
        val thumbnail: String?,
        @SerializedName("secure_media")
        val videoData: VideoData?
    ) {
        val hasImage: Boolean
            get() = url?.endsWith(".jpeg") ?: false

        val hasGallery: Boolean
            get() = galleryData != null && mediaMetadata != null

        val hasThumbnail: Boolean
            get() = thumbnail?.endsWith(".jpg") ?: false

        val hasLink: Boolean
            get() = text.isNullOrEmpty() && !url.isNullOrEmpty()

        val hasVideo: Boolean
            get() = videoData != null
    }
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

data class VideoData(
    @SerializedName("reddit_video")
    val data: Data
) {
    data class Data(
        val width: Int,
        val height: Int,
        @SerializedName("dash_url")
        val dashUrl: String,
        // @SerializedName("hls_url")
        // val hlsUrl: String,
        val duration: Int
    )
}
