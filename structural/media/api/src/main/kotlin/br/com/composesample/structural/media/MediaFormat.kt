package br.com.composesample.structural.media

/** Final content format; [extension] contains only letters and digits, without a leading dot. */
data class MediaFormat(
    val extension: String,
    val mimeType: String
)
