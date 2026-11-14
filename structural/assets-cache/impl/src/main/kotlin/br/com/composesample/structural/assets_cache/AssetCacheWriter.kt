package br.com.composesample.structural.assets_cache

import br.com.composesample.structural.media.MediaFile
import br.com.composesample.structural.media.MediaFormat
import br.com.composesample.structural.media.MediaLocation
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.network.http.BinaryDownloadProvider
import java.util.UUID
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.map

internal class AssetCacheWriter(
    private val mediaStorageProvider: MediaStorageProvider,
    private val binaryDownloadProvider: BinaryDownloadProvider,
    private val mediaLocation: MediaLocation,
    private val assetsCacheConfiguration: AssetsCacheConfiguration
) {

    suspend fun download(request: AssetRequest): MediaFile = mediaStorageProvider.createOutput(
        location = mediaLocation.copy(directory = "${mediaLocation.directory}/${request.group}"),
        nameWithoutExtension = UUID.randomUUID().toString(),
        writer = { stream ->
            binaryDownloadProvider.download(
                url = request.url,
                output = stream,
                maximumBytes = assetsCacheConfiguration.maximumBytes
            ).map { result ->
                MediaFormat(extension = result.extension, mimeType = result.mimeType)
            }
        }
    ).single()

}
