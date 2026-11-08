package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.structural.assets_cache.AssetRequest
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

enum class SampleImageLoadingStyle {
    DEFAULT,
    SHIMMER
}

/** Displays a cached image, with caller-owned unavailable content that is empty by default. */
@Composable
fun SampleImage(
    modifier: Modifier = Modifier,
    request: AssetRequest?,
    contentDescription: String?,
    loadingStyle: SampleImageLoadingStyle = SampleImageLoadingStyle.DEFAULT,
    unavailableContent: @Composable () -> Unit = {}
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
            SampleImageState.Loading -> SampleImageLoading(style = loadingStyle)
            SampleImageState.Unavailable -> unavailableContent()
        }
    }
}

@Composable
private fun SampleImageLoading(
    modifier: Modifier = Modifier,
    style: SampleImageLoadingStyle
) {
    when (style) {
        SampleImageLoadingStyle.DEFAULT -> SampleImagePlaceholder(
            modifier = modifier,
            text = stringResource(R.string.sample_image_loading)
        )
        SampleImageLoadingStyle.SHIMMER -> SampleShimmer(modifier = modifier.fillMaxSize())
    }
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

@Preview(name = "Default loading", showBackground = true)
@Composable
private fun SampleImageDefaultLoadingPreview() {
    ComposeSampleTheme {
        SampleImageLoading(
            modifier = Modifier.size(CurrentTheme.size.huge),
            style = SampleImageLoadingStyle.DEFAULT
        )
    }
}

@Preview(name = "Shimmer loading", showBackground = true)
@Composable
private fun SampleImageShimmerLoadingPreview() {
    ComposeSampleTheme {
        SampleImageLoading(
            modifier = Modifier.size(CurrentTheme.size.huge),
            style = SampleImageLoadingStyle.SHIMMER
        )
    }
}
