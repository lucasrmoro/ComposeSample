package br.com.composesample.structural.video_player

import kotlinx.coroutines.flow.Flow

/** A provider-specific video reference, not a downloadable media URL. */
data class VideoSource(val provider: String, val key: String)

/** Trusted embed document and its origin, prepared only after a user action. */
data class VideoEmbed(val baseUrl: String, val html: String)

/** Builds an embedded player without downloading or extracting video files. */
interface VideoPlaybackProvider {

    fun prepare(source: VideoSource): Flow<VideoEmbed>

}
