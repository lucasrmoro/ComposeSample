package br.com.composesample.structural.assets_cache.di

import br.com.composesample.structural.assets_cache.AssetsCacheProviderFactory
import br.com.composesample.structural.assets_cache.createAssetsCacheProviderFactory
import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton

object AssetsCacheModule {

    private val assetsCacheModule = dependencyModule {
        singleton<AssetsCacheProviderFactory> {
            createAssetsCacheProviderFactory(
                mediaStorageProvider = get(),
                binaryDownloadProvider = get(),
                coroutineDispatcher = dispatcherIO,
                hashingProvider = get(),
                fileCacheMetadataDataSource = get()
            )
        }
    }

    fun init() = loadModules(assetsCacheModule)

}
