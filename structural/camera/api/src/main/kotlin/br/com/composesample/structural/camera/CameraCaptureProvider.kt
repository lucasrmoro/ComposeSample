package br.com.composesample.structural.camera

import androidx.activity.ComponentActivity

/** Opens the device's camera application and returns the captured media. */
interface CameraCaptureProvider {

    /**
     * Launches photo capture from [activity] and returns the media reference, cancellation, or a failure.
     *
     * @param activity The resumed screen launching the camera, or `null` if it is unavailable.
     * @param destination Whether the captured media is kept in app cache or published to the gallery.
     * @param fileNameWithoutExtension Optional output base name without a file extension.
     * @throws IllegalStateException If called off the main thread or another capture is active for this screen.
     * @throws kotlinx.coroutines.CancellationException If the caller is cancelled.
     */
    suspend fun capturePhoto(
        activity: ComponentActivity?,
        destination: CameraCaptureDestination = CameraCaptureDestination.CACHE,
        fileNameWithoutExtension: String? = null
    ): CameraCaptureResult

    /**
     * Launches video capture from [activity] and returns the media reference, cancellation, or a failure.
     *
     * @param activity The resumed screen launching the camera, or `null` if it is unavailable.
     * @param destination Whether the captured media is kept in app cache or published to the gallery.
     * @param fileNameWithoutExtension Optional output base name without a file extension.
     * @throws IllegalStateException If called off the main thread or another capture is active for this screen.
     * @throws kotlinx.coroutines.CancellationException If the caller is cancelled.
     */
    suspend fun captureVideo(
        activity: ComponentActivity?,
        destination: CameraCaptureDestination = CameraCaptureDestination.CACHE,
        fileNameWithoutExtension: String? = null
    ): CameraCaptureResult

}
