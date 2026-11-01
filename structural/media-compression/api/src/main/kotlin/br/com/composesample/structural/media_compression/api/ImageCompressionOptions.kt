package br.com.composesample.structural.media_compression.api

/** Configures JPEG output quality, metadata handling, and storage behavior. */
data class ImageCompressionOptions(
    val preset: CompressionPreset = CompressionPreset.HIGH_QUALITY,
    val outputMode: CompressionOutputMode = CompressionOutputMode.REPLACE,
    /** Base name for the output, or automatic source/`_compressed` naming when `null`. */
    val outputNameWithoutExtension: String? = null,
    val locationMetadataPolicy: LocationMetadataPolicy = LocationMetadataPolicy.PRESERVE
)
