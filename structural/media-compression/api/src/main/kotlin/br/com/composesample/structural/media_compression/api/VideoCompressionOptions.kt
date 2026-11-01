package br.com.composesample.structural.media_compression.api

private const val NO_VIDEO_BITRATE_BPS = 0

/** Configures video encoder quality, bitrate, and storage behavior. */
data class VideoCompressionOptions(
    val preset: CompressionPreset = CompressionPreset.HIGH_QUALITY,
    val targetBitrate: Int? = null,
    val outputMode: CompressionOutputMode = CompressionOutputMode.REPLACE,
    /** Base name for the output, or automatic source/`_compressed` naming when `null`. */
    val outputNameWithoutExtension: String? = null
) {

    init {
        require(targetBitrate == null || targetBitrate > NO_VIDEO_BITRATE_BPS)
    }

}
