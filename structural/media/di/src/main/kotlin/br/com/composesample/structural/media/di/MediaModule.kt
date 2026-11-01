package br.com.composesample.structural.media.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.media.createMediaStorageProvider
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.permissions.PermissionQualifiers

object MediaModule {

    private val mediaModule = dependencyModule {
        singleton<MediaStorageProvider> {
            createMediaStorageProvider(
                context = get(),
                permissionsProvider = get(qualifier = PermissionQualifiers.ApplicationContext)
            )
        }
    }

    fun init() {
        loadModules(mediaModule)
    }

}
