package br.com.composesample.structural.camera

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Looper
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import br.com.composesample.structural.media.MediaLocation
import br.com.composesample.structural.media.MediaStoragePermissionRequiredException
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.media.WritableMediaFile
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Collections
import java.util.UUID
import java.util.WeakHashMap
import kotlin.coroutines.resume

internal class CameraCaptureProviderImpl(
    private val storageProvider: MediaStorageProvider
) : CameraCaptureProvider {

    private val activeActivities = Collections.synchronizedSet(
        Collections.newSetFromMap(WeakHashMap<ComponentActivity, Boolean>())
    )

    override suspend fun capturePhoto(
        activity: ComponentActivity?,
        destination: CameraCaptureDestination,
        fileNameWithoutExtension: String?
    ): CameraCaptureResult = capture(activity, CameraMediaKind.PHOTO, destination, fileNameWithoutExtension)

    override suspend fun captureVideo(
        activity: ComponentActivity?,
        destination: CameraCaptureDestination,
        fileNameWithoutExtension: String?
    ): CameraCaptureResult = capture(activity, CameraMediaKind.VIDEO, destination, fileNameWithoutExtension)

    private suspend fun capture(
        activity: ComponentActivity?,
        kind: CameraMediaKind,
        destination: CameraCaptureDestination,
        requestedName: String?
    ): CameraCaptureResult {
        val screen = activity ?: return CameraCaptureResult.Failed(CameraCaptureFailure.ACTIVITY_UNAVAILABLE)
        check(Looper.myLooper() == Looper.getMainLooper()) { "Camera capture must be requested on the main thread" }
        if (!screen.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            return CameraCaptureResult.Failed(CameraCaptureFailure.ACTIVITY_UNAVAILABLE)
        }
        check(activeActivities.add(screen)) { "A camera capture is already in progress for this screen" }
        val name = requestedName ?: "$CAPTURE_FILE_NAME_PREFIX${UUID.randomUUID()}"
        val output = try {
            storageProvider.createOutput(
                location = when (destination) {
                    CameraCaptureDestination.CACHE -> MediaLocation.appCache(CAMERA_CACHE_DIRECTORY)
                    CameraCaptureDestination.GALLERY ->
                        MediaLocation.gallery("${kind.publicDirectory}/$CAMERA_GALLERY_DIRECTORY")
                },
                nameWithoutExtension = name,
                extension = kind.extension,
                mimeType = kind.mimeType
            )
        } catch (_: MediaStoragePermissionRequiredException) {
            activeActivities.remove(screen)
            return CameraCaptureResult.Failed(CameraCaptureFailure.PERMISSION_REQUIRED)
        } catch (_: Exception) {
            activeActivities.remove(screen)
            return CameraCaptureResult.Failed(CameraCaptureFailure.OUTPUT_UNAVAILABLE)
        }
        return suspendCancellableCoroutine { continuation ->
            CaptureSession(screen, output, continuation).launch(kind.action)
        }
    }

    private inner class CaptureSession(
        private val screen: ComponentActivity,
        private val output: WritableMediaFile,
        private val continuation: CancellableContinuation<CameraCaptureResult>
    ) {

        private var launcher: ActivityResultLauncher<CameraCaptureRequest>? = null
        private var finished = false
        private var resultReceived = false
        private val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY && !resultReceived) finish(null, discard = true)
        }

        fun launch(action: String) {
            screen.lifecycle.addObserver(observer)
            try {
                val registered = screen.activityResultRegistry.register(
                    "$ACTIVITY_RESULT_KEY_PREFIX${UUID.randomUUID()}",
                    CameraCaptureIntentContract(),
                    ::onResult
                )
                launcher = registered
                registered.launch(CameraCaptureRequest(action, Uri.parse(output.uri)))
            } catch (_: ActivityNotFoundException) {
                finish(CameraCaptureResult.Failed(CameraCaptureFailure.CAMERA_UNAVAILABLE), discard = true)
            } catch (_: Exception) {
                finish(CameraCaptureResult.Failed(CameraCaptureFailure.CAPTURE_FAILED), discard = true)
            }
        }

        private fun onResult(success: Boolean) {
            resultReceived = true
            val result = when {
                !success -> CameraCaptureResult.Cancelled
                output.sizeBytes <= 0L -> CameraCaptureResult.Failed(CameraCaptureFailure.CAPTURE_FAILED)
                !output.publish() -> CameraCaptureResult.Failed(CameraCaptureFailure.CAPTURE_FAILED)
                else -> CameraCaptureResult.Captured(output)
            }
            finish(result, discard = result !is CameraCaptureResult.Captured)
        }

        private fun finish(result: CameraCaptureResult?, discard: Boolean) {
            if (finished) return
            finished = true
            screen.lifecycle.removeObserver(observer)
            launcher?.unregister()
            activeActivities.remove(screen)
            if (discard || !continuation.isActive) output.discard()
            if (result == null) {
                if (continuation.isActive) continuation.cancel()
            } else if (continuation.isActive) continuation.resume(result)
        }

    }

    private companion object {
        const val CAPTURE_FILE_NAME_PREFIX = "capture_"
        const val CAMERA_CACHE_DIRECTORY = "camera"
        const val CAMERA_GALLERY_DIRECTORY = "Camera"
        const val ACTIVITY_RESULT_KEY_PREFIX = "camera_capture_"
    }

}

private enum class CameraMediaKind(
    val action: String,
    val extension: String,
    val mimeType: String,
    val publicDirectory: String
) {
    PHOTO(MediaStore.ACTION_IMAGE_CAPTURE, "jpg", "image/jpeg", android.os.Environment.DIRECTORY_PICTURES),
    VIDEO(MediaStore.ACTION_VIDEO_CAPTURE, "mp4", "video/mp4", android.os.Environment.DIRECTORY_MOVIES)
}
