package br.com.composesample.structural.camera

/** Identifies why a camera capture request failed. */
enum class CameraCaptureFailure {
    ACTIVITY_UNAVAILABLE,
    CAMERA_UNAVAILABLE,
    PERMISSION_REQUIRED,
    OUTPUT_UNAVAILABLE,
    CAPTURE_FAILED
}
