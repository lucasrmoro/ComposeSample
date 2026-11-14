package br.com.composesample.stream.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaSummary
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme

internal const val RATING_FORMAT = "%.1f"

@Composable
internal fun SummaryCard(
    modifier: Modifier = Modifier,
    item: MediaSummary,
    kind: CollectionKind,
    onClick: () -> Unit
) {
    SampleMediaCard(
        modifier = modifier,
        title = item.title,
        imageRequest = imageRequest(item.posterPath),
        rating = item.rating?.let { String.format(java.util.Locale.getDefault(), RATING_FORMAT, it) },
        year = item.year.takeUnless { kind == CollectionKind.RECENT },
        certification = item.certification,
        releaseLabel = null,
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
private fun SummaryCardPreview() {
    ComposeSampleTheme {
        SummaryCard(
            item = MediaSummary(
                id = MediaId(type = MediaType.MOVIE, value = 1),
                title = "Beyond the horizon",
                posterPath = null,
                year = "2026",
                rating = 7.8,
                certification = "16"
            ),
            kind = CollectionKind.POPULAR,
            onClick = {}
        )
    }
}
