package br.com.composesample.structural.camera

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

internal class CameraCaptureIntentContract : ActivityResultContract<CameraCaptureRequest, Boolean>() {

    override fun createIntent(context: Context, input: CameraCaptureRequest): Intent =
        Intent(input.action).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, input.outputUri)
            clipData = ClipData.newRawUri(CAMERA_CAPTURE_CLIP_LABEL, input.outputUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean = resultCode == Activity.RESULT_OK

    private companion object {
        const val CAMERA_CAPTURE_CLIP_LABEL = "camera_capture"
    }

}

internal data class CameraCaptureRequest(
    val action: String,
    val outputUri: Uri
)
