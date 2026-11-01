package br.com.composesample.structural.media_compression.impl

internal object MediaCompressionConstants {

    const val IMAGE_MIME_PREFIX = "image/"
    const val VIDEO_MIME_PREFIX = "video/"
    const val JPEG_EXTENSION = "jpg"
    const val JPEG_MIME_TYPE = "image/jpeg"
    const val MP4_EXTENSION = "mp4"
    const val MP4_MIME_TYPE = "video/mp4"
    const val COMPRESSED_COPY_NAME_SUFFIX = "_compressed"
    const val REPLACEMENT_NAME_INFIX = "_working_"
    const val IMAGE_TEMP_FILE_PREFIX = "image_compress_"
    const val VIDEO_INPUT_TEMP_FILE_PREFIX = "video_input_"
    const val VIDEO_OUTPUT_TEMP_FILE_PREFIX = "video_output_"
    const val MINIMUM_VIDEO_BITRATE_BPS = 128_000

}
