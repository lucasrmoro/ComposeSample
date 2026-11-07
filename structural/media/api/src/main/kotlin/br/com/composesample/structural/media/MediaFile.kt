package br.com.composesample.structural.media

import java.io.IOException
import java.io.InputStream

/** Describes a media item without exposing its platform storage implementation. */
interface MediaFile {

    /** Base file name without its extension. */
    val nameWithoutExtension: String

    /** File extension without a leading dot. */
    val extension: String

    /** MIME type of the media content. */
    val mimeType: String

    /** URI string suitable for platform media loading. */
    val uri: String

    /** Byte count currently stored for this media item. */
    val sizeBytes: Long

    /** Storage area and directory containing this item. */
    val location: MediaLocation

    /** Opens this item for reading. The caller must close the returned stream. */
    @Throws(IOException::class) fun openStream(): InputStream

    /** Deletes this item from its storage location. */
    fun delete(): Boolean

}
