package br.com.composesample.structural.media_compression.api

/** Describes why compression could not produce a smaller output. */
enum class CompressionFailure {
    UNSUPPORTED_FORMAT,
    INVALID_MEDIA,
    NO_SIZE_REDUCTION,
    PROCESSING_FAILED,
    STORAGE_FAILED
}
