package br.com.composesample.structural.media_compression.api

private const val HIGH_QUALITY_IMAGE_QUALITY = 70
private const val BALANCED_IMAGE_QUALITY = 65
private const val COMPACT_IMAGE_QUALITY = 60
private const val HIGH_QUALITY_VIDEO_BITRATE_RATIO = 0.9
private const val BALANCED_VIDEO_BITRATE_RATIO = 0.75
private const val COMPACT_VIDEO_BITRATE_RATIO = 0.55

/** Sets the intended balance between perceptual quality and output size. */
enum class CompressionPreset(
    /** JPEG encoder quality selected by this preset. */
    val imageQuality: Int,
    /** Multiplier applied to the source video bitrate when no override is set. */
    val videoBitrateRatio: Double
) {
    HIGH_QUALITY(
        imageQuality = HIGH_QUALITY_IMAGE_QUALITY,
        videoBitrateRatio = HIGH_QUALITY_VIDEO_BITRATE_RATIO
    ),
    BALANCED(
        imageQuality = BALANCED_IMAGE_QUALITY,
        videoBitrateRatio = BALANCED_VIDEO_BITRATE_RATIO
    ),
    COMPACT(
        imageQuality = COMPACT_IMAGE_QUALITY,
        videoBitrateRatio = COMPACT_VIDEO_BITRATE_RATIO
    )
}
