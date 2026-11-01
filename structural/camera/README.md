# Camera capture

The camera service launches the device's camera application to capture a photo
or video. Consumers depend on `camera/api`; Android intents, `FileProvider`, and
content resolver access stay in `camera/impl`. `camera/di` registers the provider,
and `AppApplication` loads `CameraModule.init()` with the structural
modules.

## Usage

Inject `CameraCaptureProvider` without runtime parameters. Pass the current
screen to the capture method, choose a destination, and optionally set a base
file name without an extension:

```kotlin
import br.com.composesample.structural.camera.CameraCaptureDestination
import br.com.composesample.structural.camera.CameraCaptureProvider

private val cameraCaptureProvider by inject<CameraCaptureProvider>()

fun capturePhoto() {
    lifecycleScope.launch {
        when (val result = cameraCaptureProvider.capturePhoto(
            activity = this@CameraActivity,
            destination = CameraCaptureDestination.GALLERY,
            fileNameWithoutExtension = "profile_photo"
        )) {
            is CameraCaptureResult.Captured -> {
                val media = result.media
                // Load Uri.parse(media.uri) in the screen's image loader.
                media.openStream().use { input -> upload(input) }
            }
            CameraCaptureResult.Cancelled -> Unit
            is CameraCaptureResult.Failed -> showCaptureError(result.reason)
        }
    }
}
```

Use `captureVideo()` for video. Both methods default to `CACHE`; pass `GALLERY`
to publish photos under `Pictures` or videos under `Movies`. A captured result
is a shared `MediaFile`, with a content URI string for UI loaders, a MIME type
for upload metadata, and `openStream()` for upload or processing. Close every
opened stream. The caller may explicitly pass it to
`MediaCompressionProvider`; capture does not compress media implicitly.

On Android 10 and later, gallery captures are inserted through `MediaStore`. On
Android 8 and 9, the provider returns `PERMISSION_REQUIRED` until the screen has
obtained `Permission.WRITE_EXTERNAL_STORAGE` through the shared permission
service. The camera service does not request that permission itself.

## Media lifetime

Successful cache captures are stored in the app's private cache. Gallery captures
are published to shared device storage. The caller owns the reference and can
call `MediaFile.delete()` when the capture is no longer needed. Android may
clear cache files at any time, so copy media that must survive cache cleanup.
Cancelled or failed captures are deleted by the service when the camera returns.

Capture calls require a resumed `ComponentActivity` and allow one active request
per screen. Pass `null` when no screen is available; the result is
`ACTIVITY_UNAVAILABLE`. The shared media service registers a non-exported
`FileProvider` to grant the camera app temporary access to cache output.

The API keeps Android types out of its public signatures. A future integrated
camera experience can be added as another implementation without changing
feature ownership of captured media.
