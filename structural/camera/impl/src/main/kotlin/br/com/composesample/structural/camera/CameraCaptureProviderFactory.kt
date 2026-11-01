package br.com.composesample.structural.camera

import br.com.composesample.structural.media.MediaStorageProvider

/** Creates a camera provider backed by the shared media storage service. */
fun createCameraCaptureProvider(storageProvider: MediaStorageProvider): CameraCaptureProvider =
    CameraCaptureProviderImpl(storageProvider)
