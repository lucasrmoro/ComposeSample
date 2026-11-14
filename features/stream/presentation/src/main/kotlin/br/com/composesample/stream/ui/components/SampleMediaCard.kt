package br.com.composesample.stream.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.stream.tokens.StreamDimens
import br.com.composesample.structural.assets_cache.AssetRequest
import br.com.composesample.structural.designsystem.components.SampleImage
import br.com.composesample.structural.designsystem.components.SampleIcon
import br.com.composesample.structural.designsystem.components.SampleSurface
import br.com.composesample.structural.designsystem.components.SampleText
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
        SampleText(text = title, style = CurrentTheme.typography.titleSmall)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.xSmall)
        ) {
            rating?.let { rating ->
                SampleIcon(
                    modifier = Modifier.size(StreamDimens.ratingIcon),
                    icon = CurrentTheme.icon.star,
                    contentDescription = null,
                    tint = CurrentTheme.colors.primary
                )
                SampleText(text = rating, style = CurrentTheme.typography.labelSmall)
            }
            year?.let { SampleText(text = it, style = CurrentTheme.typography.labelSmall) }
            certification?.let { certification ->
                SampleSurface(
                    shape = RoundedCornerShape(CurrentTheme.size.xxxSmall),
                    outlined = true
                ) {
                    SampleText(
                        modifier = Modifier.padding(horizontal = CurrentTheme.spacing.xSmall),
                        text = certification,
                        style = CurrentTheme.typography.labelSmall
                    )
                }
            }
        }
        releaseLabel?.let {
            SampleText(
                text = it,
                style = CurrentTheme.typography.labelSmall,
                color = CurrentTheme.color.primary
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
