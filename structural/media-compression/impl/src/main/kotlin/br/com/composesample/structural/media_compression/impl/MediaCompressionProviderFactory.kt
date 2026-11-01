package br.com.composesample.structural.media_compression.impl

import android.content.Context
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.media_compression.api.MediaCompressionProvider
import kotlinx.coroutines.CoroutineDispatcher

fun createMediaCompressionProvider(
    context: Context,
    storageProvider: MediaStorageProvider,
    ioDispatcher: CoroutineDispatcher
): MediaCompressionProvider = MediaCompressionProviderImpl(context, storageProvider, ioDispatcher)
