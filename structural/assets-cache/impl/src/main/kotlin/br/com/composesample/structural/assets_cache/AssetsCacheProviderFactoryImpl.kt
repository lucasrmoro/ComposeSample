package br.com.composesample.structural.assets_cache

import br.com.composesample.structural.local_storage.cache.FileCacheMetadataDataSource
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.network.http.BinaryDownloadProvider
import br.com.composesample.structural.security.hashing.HashingProvider
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineDispatcher

internal class AssetsCacheProviderFactoryImpl(
    private val mediaStorageProvider: MediaStorageProvider,
    private val binaryDownloadProvider: BinaryDownloadProvider,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val hashingProvider: HashingProvider,
    private val fileCacheMetadataDataSource: FileCacheMetadataDataSource
) : AssetsCacheProviderFactory {

    private val providers = ConcurrentHashMap<AssetsCacheConfiguration, AssetsCacheProvider>()

    override fun create(configuration: AssetsCacheConfiguration): AssetsCacheProvider =
        providers.computeIfAbsent(configuration) {
            createAssetsCacheProvider(
                mediaStorageProvider = mediaStorageProvider,
                binaryDownloadProvider = binaryDownloadProvider,
                coroutineDispatcher = coroutineDispatcher,
                assetsCacheConfiguration = configuration,
                hashingProvider = hashingProvider,
                fileCacheMetadataDataSource = fileCacheMetadataDataSource
            )
        }

}

fun createAssetsCacheProviderFactory(
    mediaStorageProvider: MediaStorageProvider,
    binaryDownloadProvider: BinaryDownloadProvider,
    coroutineDispatcher: CoroutineDispatcher,
    hashingProvider: HashingProvider,
    fileCacheMetadataDataSource: FileCacheMetadataDataSource
): AssetsCacheProviderFactory =
    AssetsCacheProviderFactoryImpl(
        mediaStorageProvider = mediaStorageProvider,
        binaryDownloadProvider = binaryDownloadProvider,
        coroutineDispatcher = coroutineDispatcher,
        hashingProvider = hashingProvider,
        fileCacheMetadataDataSource = fileCacheMetadataDataSource
    )
