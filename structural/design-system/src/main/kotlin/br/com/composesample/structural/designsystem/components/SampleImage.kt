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
        when (state) {
            is SampleImageState.Loaded -> Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = state.bitmap,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
            SampleImageState.Loading -> SampleImageLoading()
            SampleImageState.Unavailable -> SampleImagePlaceholder(
                text = stringResource(R.string.sample_image_unavailable)
            )
        }
    }
}

@Composable
private fun SampleImageLoading(modifier: Modifier = Modifier) {
    SampleImagePlaceholder(
        modifier = modifier,
        text = stringResource(R.string.sample_image_loading)
    )
}

@Composable
private fun SampleImagePlaceholder(modifier: Modifier = Modifier, text: String) {
    SampleText(
        modifier = modifier,
        text = text,
        style = CurrentTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
