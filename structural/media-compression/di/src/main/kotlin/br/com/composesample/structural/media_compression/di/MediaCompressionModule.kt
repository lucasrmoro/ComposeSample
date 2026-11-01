package br.com.composesample.structural.media_compression.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.media_compression.api.MediaCompressionProvider
import br.com.composesample.structural.media_compression.impl.createMediaCompressionProvider

object MediaCompressionModule {

    private val mediaCompressionModule = dependencyModule {
        singleton<MediaCompressionProvider> {
            createMediaCompressionProvider(
                context = get(),
                storageProvider = get<MediaStorageProvider>(),
                ioDispatcher = dispatcherIO
            )
        }
    }

    fun init() {
        loadModules(mediaCompressionModule)
    }

}
