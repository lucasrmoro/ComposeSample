package br.com.composesample.structural.camera

/** Selects where a successful capture is stored. */
enum class CameraCaptureDestination {
    /** Stores media in the app's private cache. Android may clear it at any time. */
    CACHE,

    /** Publishes media to the device gallery under Pictures or Movies. */
    GALLERY
}
