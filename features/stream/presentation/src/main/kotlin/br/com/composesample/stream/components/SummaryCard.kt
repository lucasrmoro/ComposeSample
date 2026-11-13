package br.com.composesample.stream.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaSummary
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.model.releases.ReleaseKind
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme

internal const val RATING_FORMAT = "%.1f"

@Composable
internal fun SummaryCard(
    modifier: Modifier = Modifier,
    item: MediaSummary,
    kind: CollectionKind,
    onClick: () -> Unit
) {
    val label =
        item.releaseEvent?.let { event ->
            val season = event.season
            val episode = event.episode
            when (event.kind) {
                ReleaseKind.NEW_SERIES -> stringResource(R.string.stream_new_series)
                ReleaseKind.NEW_SEASON -> season?.let {
                    stringResource(R.string.stream_new_season, it)
                } ?: stringResource(R.string.stream_new_season_generic)
                ReleaseKind.NEW_EPISODE -> season?.let { seasonNumber ->
                    episode?.let { episodeNumber ->
                        stringResource(R.string.stream_new_episode, seasonNumber, episodeNumber)
                    }
                } ?: stringResource(R.string.stream_new_episode_generic)
            }
        }
    SampleMediaCard(
        modifier = modifier,
        title = item.title,
        imageRequest = imageRequest(item.posterPath),
        rating = item.rating?.let { String.format(java.util.Locale.getDefault(), RATING_FORMAT, it) },
        year = item.year.takeUnless { kind == CollectionKind.RECENT },
        certification = item.certification,
        releaseLabel = label,
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
                certification = "16",
                releaseEvent = null
            ),
            kind = CollectionKind.POPULAR,
            onClick = {}
        )
    }
}
