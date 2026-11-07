package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import br.com.composesample.structural.designsystem.tokens.Dimen
import br.com.composesample.structural.assets_cache.AssetRequest

@Composable
fun MediaCardSample(
    title: String,
    imageRequest: AssetRequest?,
    rating: String?,
    year: String?,
    certification: String?,
    releaseLabel: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.width(Dimen.Size.PosterWidth).clickable(onClick = onClick)) {
        ImageSample(
            imageRequest,
            title,
            Modifier.fillMaxWidth()
                .height(Dimen.Size.PosterHeight)
                .clip(RoundedCornerShape(Dimen.Radius.Medium))
        )
        Spacer(Modifier.height(Dimen.Spacing.Small))
        Text(title, style = MaterialTheme.typography.titleSmall)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimen.Spacing.ExtraSmall)
        ) {
            rating?.let { rating ->
                Icon(
                    Icons.Default.Star,
                    null,
                    Modifier.size(Dimen.Size.RatingIcon),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(rating, style = MaterialTheme.typography.labelSmall)
            }
            year?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
            certification?.let { certification ->
                Surface(
                    shape = RoundedCornerShape(Dimen.Radius.Small),
                    border = BorderStroke(Dimen.Stroke.Thin, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        certification,
                        Modifier.padding(horizontal = Dimen.Spacing.ExtraSmall),
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
