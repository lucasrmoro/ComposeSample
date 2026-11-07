package br.com.composesample.structural.media

import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

/** Supplies content and its final format without taking ownership of the destination stream. */
fun interface MediaContentWriter {

    /**
     * Writes to [output] and emits exactly one format. Collection must complete only after all
     * writing finishes, cooperate with cancellation, and leave stream closure to the provider.
     */
    fun write(output: OutputStream): Flow<MediaFormat>

}
