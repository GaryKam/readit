package io.github.garykam.readit.data.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import io.github.garykam.readit.data.source.remote.RedditPostCommentAdapterFactory

data class RedditPostComment(
    val data: Data
) {
    data class Data(
        @SerializedName("name")
        val id: String,
        val author: String,
        val title: String,
        @SerializedName("selftext_html")
        val header: String?,
        @SerializedName("body_html")
        val text: String?,
        val domain: String?,
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
        val videoData: VideoData?,
        @JsonAdapter(RedditPostCommentAdapterFactory::class)
        val replies: RedditListing<RedditPostComment>?
    ) {
        val hasImage: Boolean
            get() = url?.endsWith(".jpeg") ?: false

        val hasGallery: Boolean
            get() = galleryData != null && mediaMetadata != null

        val hasThumbnail: Boolean
            get() = !domain.isNullOrEmpty() && thumbnail?.endsWith(".jpg") ?: false

        val hasVideo: Boolean
            get() = videoData != null
    }
}
