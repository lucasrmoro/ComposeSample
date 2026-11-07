package br.com.composesample.structural.video_player

import java.net.URI
import kotlinx.coroutines.flow.flow

internal class VideoPlaybackProviderImpl(
    private val videoPlaybackConfiguration: VideoPlaybackConfiguration
) : VideoPlaybackProvider {

    override fun prepare(source: VideoSource) = flow {
        require(source.provider.equals(YOUTUBE_PROVIDER, ignoreCase = true)) {
            UNSUPPORTED_PROVIDER_MESSAGE
        }
        require(source.key.matches(Regex(VIDEO_KEY_PATTERN))) { INVALID_KEY_MESSAGE }
        val origin = resolveOrigin()
        emit(
            VideoEmbed(
                baseUrl = origin,
                html =
                    EMBED_DOCUMENT.replace(VIDEO_KEY_PLACEHOLDER, source.key)
                        .replace(ORIGIN_PLACEHOLDER, origin)
            )
        )
    }

    private fun resolveOrigin(): String {
        val origin = URI(videoPlaybackConfiguration.origin.removeSuffix("/"))
        require(
            origin.scheme.equals(HTTPS_SCHEME, ignoreCase = true) &&
                !origin.host.isNullOrBlank() && origin.rawUserInfo == null &&
                origin.rawPath.orEmpty().isEmpty() && origin.rawQuery == null &&
                origin.rawFragment == null
        ) { INVALID_ORIGIN_MESSAGE }
        return origin.toASCIIString()
    }

    private companion object {
        const val YOUTUBE_PROVIDER = "YouTube"
        const val UNSUPPORTED_PROVIDER_MESSAGE = "Unsupported video provider"
        const val VIDEO_KEY_PATTERN = "[A-Za-z0-9_-]{11}"
        const val INVALID_KEY_MESSAGE = "Invalid video key"
        const val HTTPS_SCHEME = "https"
        const val INVALID_ORIGIN_MESSAGE = "Video embed origin must be an HTTPS origin without credentials, path, query or fragment"
        const val VIDEO_KEY_PLACEHOLDER = "{{videoKey}}"
        const val ORIGIN_PLACEHOLDER = "{{origin}}"
        const val EMBED_DOCUMENT =
            """<html><head><meta name="viewport" content="width=device-width,initial-scale=1"><meta name="referrer" content="strict-origin-when-cross-origin"><style>body{margin:0;background:black}iframe{width:100%;height:100%;border:0}</style></head><body><iframe src="https://www.youtube.com/embed/{{videoKey}}?playsinline=1&amp;autoplay=0&amp;origin={{origin}}" allow="encrypted-media; fullscreen; picture-in-picture" allowfullscreen></iframe></body></html>"""
    }
}

fun createVideoPlaybackProvider(
    videoPlaybackConfiguration: VideoPlaybackConfiguration
): VideoPlaybackProvider = VideoPlaybackProviderImpl(videoPlaybackConfiguration)
