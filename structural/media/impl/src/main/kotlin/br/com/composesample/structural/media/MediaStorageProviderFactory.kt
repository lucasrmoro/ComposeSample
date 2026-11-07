package br.com.composesample.structural.media

import android.content.Context
import br.com.composesample.structural.permissions.PermissionsProvider
import kotlinx.coroutines.CoroutineDispatcher

fun createMediaStorageProvider(
    context: Context,
    permissionsProvider: PermissionsProvider,
    coroutineDispatcher: CoroutineDispatcher
): MediaStorageProvider =
    MediaStorageProviderImpl(context, permissionsProvider, coroutineDispatcher)
