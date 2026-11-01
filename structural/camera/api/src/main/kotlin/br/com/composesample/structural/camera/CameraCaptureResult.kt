package br.com.composesample.structural.camera

/** Describes the outcome of a request to capture media with the device's camera application. */
sealed interface CameraCaptureResult {

    /** The camera application created a readable media file. */
    data class Captured(val media: br.com.composesample.structural.media.MediaFile) : CameraCaptureResult

    /** The user or camera application ended capture without producing media. */
    data object Cancelled : CameraCaptureResult

    /** The request could not be completed. */
    data class Failed(val reason: CameraCaptureFailure) : CameraCaptureResult

}
