package br.com.composesample.stream.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.stream.tokens.StreamDimens
import br.com.composesample.structural.assets_cache.AssetRequest
import br.com.composesample.structural.designsystem.components.SampleImage
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
fun SampleMediaCard(
    modifier: Modifier = Modifier,
    title: String,
    imageRequest: AssetRequest?,
    rating: String?,
    year: String?,
    certification: String?,
    releaseLabel: String?,
    onClick: () -> Unit
) {
    Column(modifier.width(StreamDimens.posterWidth).clickable(onClick = onClick)) {
        SampleImage(
            request = imageRequest,
            contentDescription = title,
            modifier = Modifier.fillMaxWidth()
                .height(StreamDimens.posterHeight)
                .clip(RoundedCornerShape(CurrentTheme.size.xSmall))
        )
        Spacer(Modifier.height(CurrentTheme.spacing.small))
        Text(title, style = MaterialTheme.typography.titleSmall)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.xSmall)
        ) {
            rating?.let { rating ->
                Icon(
                    Icons.Default.Star,
                    null,
                    Modifier.size(StreamDimens.ratingIcon),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(rating, style = MaterialTheme.typography.labelSmall)
            }
            year?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
            certification?.let { certification ->
                Surface(
                    shape = RoundedCornerShape(CurrentTheme.size.xxxSmall),
                    border = BorderStroke(CurrentTheme.stroke.thin, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        certification,
                        Modifier.padding(horizontal = CurrentTheme.spacing.xSmall),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        releaseLabel?.let {
            Text(
                it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleMediaCardPreview() {
    ComposeSampleTheme {
        SampleMediaCard(
            title = "Beyond the horizon",
            imageRequest = null,
            rating = "7.8",
            year = "2026",
            certification = "16",
            releaseLabel = "New season",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleMediaCardMinimalPreview() {
    ComposeSampleTheme {
        SampleMediaCard(
            title = "Hidden valley",
            imageRequest = null,
            rating = null,
            year = null,
            certification = null,
            releaseLabel = null,
            onClick = {}
        )
    }
}
