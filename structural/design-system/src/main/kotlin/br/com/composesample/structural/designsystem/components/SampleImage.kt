package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import br.com.composesample.structural.assets_cache.AssetRequest
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.CurrentTheme

/** Displays a cached image or its loading and unavailable placeholders. */
@Composable
fun SampleImage(
    modifier: Modifier = Modifier,
    request: AssetRequest?,
    contentDescription: String?
) {
    val state = rememberSampleImageState(request)
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val bitmap = state.bitmap
        if (bitmap != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = bitmap,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
        } else {
            SampleText(
                text = stringResource(
                    if (state.isUnavailable) R.string.sample_image_unavailable else R.string.sample_image_loading
                ),
                style = CurrentTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
