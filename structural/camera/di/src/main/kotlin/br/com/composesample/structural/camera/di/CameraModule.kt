package br.com.composesample.structural.camera.di

import br.com.composesample.structural.camera.CameraCaptureProvider
import br.com.composesample.structural.camera.createCameraCaptureProvider
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton

object CameraModule {

    private val cameraModule = dependencyModule {
        singleton<CameraCaptureProvider> {
            createCameraCaptureProvider(
                storageProvider = get<MediaStorageProvider>()
            )
        }
    }

    fun init() {
        loadModules(cameraModule)
    }

}
