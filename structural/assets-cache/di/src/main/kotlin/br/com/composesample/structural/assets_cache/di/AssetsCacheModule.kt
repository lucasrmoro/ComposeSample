package br.com.composesample.structural.assets_cache.di

import br.com.composesample.structural.assets_cache.*
import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.di.*

object AssetsCacheModule {

    private val assetsCacheModule = dependencyModule {
        singleton<AssetsCacheProvider> {
            createAssetsCacheProvider(
                mediaStorageProvider = get(),
                binaryDownloadProvider = get(),
                coroutineDispatcher = dispatcherIO,
                assetsCacheConfiguration = get(),
                hashingProvider = get(),
                fileCacheMetadataDataSource = get()
            )
        }
    }

    fun init() = loadModules(assetsCacheModule)

}
