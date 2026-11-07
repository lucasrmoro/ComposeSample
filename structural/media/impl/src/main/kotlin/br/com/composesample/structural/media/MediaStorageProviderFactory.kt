package br.com.composesample.structural.media

import android.content.Context
import br.com.composesample.structural.permissions.PermissionsProvider
import kotlinx.coroutines.CoroutineDispatcher

fun createMediaStorageProvider(
    context: Context,
    permissionsProvider: PermissionsProvider,
    coroutineDispatcher: CoroutineDispatcher
): MediaStorageProvider {
    val applicationContext = context.applicationContext
    val paths = MediaStoragePaths(applicationContext)
    return MediaStorageProviderImpl(
        context = applicationContext,
        permissionsProvider = permissionsProvider,
        paths = paths,
        cacheStore = MediaCacheStore(applicationContext, paths, coroutineDispatcher),
        galleryStore = MediaGalleryStore(applicationContext, paths)
    )
}
