package br.com.composesample.structural.media

import android.content.Context
import br.com.composesample.structural.permissions.PermissionsProvider

fun createMediaStorageProvider(
    context: Context,
    permissionsProvider: PermissionsProvider
): MediaStorageProvider = MediaStorageProviderImpl(context, permissionsProvider)
