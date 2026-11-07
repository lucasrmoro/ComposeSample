package br.com.composesample.structural.video_player

import android.content.Context
import java.net.URI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

internal class VideoPlaybackProviderImpl(
    private val videoPlaybackConfiguration: VideoPlaybackConfiguration,
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) : VideoPlaybackProvider {

    override fun prepare(source: VideoSource) = flow {
        require(source.provider.equals(YOUTUBE_PROVIDER, ignoreCase = true)) {
            UNSUPPORTED_PROVIDER_MESSAGE
        }
        require(source.key.matches(Regex(VIDEO_KEY_PATTERN))) { INVALID_KEY_MESSAGE }
        val origin = resolveOrigin()
        val document = withContext(ioDispatcher) {
            context.assets.open(EMBED_ASSET).bufferedReader().use { it.readText() }
        }
        emit(
            VideoEmbed(
                baseUrl = origin,
                html =
                    document.replace(VIDEO_KEY_PLACEHOLDER, source.key)
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
        const val EMBED_ASSET = "embed.html"
    }
}

fun createVideoPlaybackProvider(
    videoPlaybackConfiguration: VideoPlaybackConfiguration,
    context: Context,
    ioDispatcher: CoroutineDispatcher
): VideoPlaybackProvider = VideoPlaybackProviderImpl(videoPlaybackConfiguration, context, ioDispatcher)
