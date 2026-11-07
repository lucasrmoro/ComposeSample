package br.com.composesample.structural.media

import java.io.IOException
import java.io.OutputStream

/** A media item being written and not yet published to gallery readers. */
interface WritableMediaFile : MediaFile {

    /** Opens this item for writing. The caller must close the returned stream. */
    @Throws(IOException::class) fun openOutputStream(): OutputStream

    /** Makes a completed item visible in gallery storage. */
    fun publish(): Boolean

    /** Removes an incomplete item after a failed or cancelled operation. */
    fun discard(): Boolean

}
