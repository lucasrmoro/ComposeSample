package br.com.composesample.stream.components

import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaSummary
import br.com.composesample.stream.model.releases.ReleaseKind
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.structural.core.text.UiText
import br.com.composesample.stream.components.SampleMediaCard
import br.com.composesample.structural.designsystem.text.asString
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.stream.tokens.StreamDimens
import br.com.composesample.structural.assets_cache.AssetRequest

internal enum class ImageRole(val group: String) {
    POSTER("posters"),
    BACKDROP("backdrops"),
    PROFILE("cast"),
    LOGO("providers")
}

internal fun imageRequest(path: String?, role: ImageRole = ImageRole.POSTER): AssetRequest? =
    path
        ?.takeIf { it.startsWith(PATH_PREFIX) && !it.contains(PARENT_PATH) }
        ?.let {
            val size =
                when (role) {
                    ImageRole.POSTER -> POSTER_SIZE
                    ImageRole.BACKDROP -> BACKDROP_SIZE
                    ImageRole.PROFILE -> PROFILE_SIZE
                    ImageRole.LOGO -> LOGO_SIZE
                }
            AssetRequest(url = "$IMAGE_BASE_URL/$size$it", group = role.group)
        }

@Composable
internal fun collectionTitle(kind: CollectionKind): String =
    stringResource(
        when (kind) {
            CollectionKind.POPULAR -> R.string.stream_popular
            CollectionKind.RECENT -> R.string.stream_recent
            CollectionKind.UPCOMING -> R.string.stream_upcoming
            CollectionKind.GENRE -> R.string.stream_genres
            CollectionKind.SEARCH -> R.string.stream_results
        }
    )

@Composable
internal fun SummaryCard(
    item: MediaSummary,
    kind: CollectionKind,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label =
        item.releaseEvent?.let { event ->
            val season = event.season
            val episode = event.episode
            when (event.kind) {
                ReleaseKind.NEW_SERIES -> stringResource(R.string.stream_new_series)
                ReleaseKind.NEW_SEASON ->
                    if (season != null) stringResource(R.string.stream_new_season, season)
                    else stringResource(R.string.stream_new_season_generic)
                ReleaseKind.NEW_EPISODE ->
                    if (season != null && episode != null)
                        stringResource(R.string.stream_new_episode, season, episode)
                    else stringResource(R.string.stream_new_episode_generic)
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

@Composable
internal fun LoadNotice(loading: Boolean, empty: Boolean, error: UiText?, retry: () -> Unit) {
    if (loading && empty) CircularProgressIndicator()
    if (error != null) {
        Text(error.asString(), style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = retry) { Text(stringResource(R.string.stream_retry)) }
    } else if (empty && !loading) Text(stringResource(R.string.stream_empty))
}

@Composable
internal fun MediaGrid(
    page: CatalogPage,
    kind: CollectionKind,
    paging: Boolean,
    onDetails: (MediaId) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberLazyGridState()
    val last by remember {
        derivedStateOf { scroll.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
    }
    LaunchedEffect(last, page.items.size, page.hasNextPage, paging) {
        if (last >= page.items.size - 3 && page.items.isNotEmpty() && page.hasNextPage && !paging)
            onNext()
    }
    LazyVerticalGrid(
        GridCells.Fixed(2),
        modifier,
        state = scroll,
        horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.large),
        contentPadding = PaddingValues(CurrentTheme.spacing.large)
    ) {
        items(page.items, key = { "${it.id.type}:${it.id.value}" }) { item ->
            SummaryCard(item, kind, { onDetails(item.id) }, Modifier.fillMaxWidth())
        }
        if (paging) item { CircularProgressIndicator() }
        if (page.hasNextPage && !paging)
            item { TextButton(onClick = onNext) { Text(stringResource(R.string.stream_more)) } }
    }
}

internal const val RATING_FORMAT = "%.1f"
private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
private const val BACKDROP_SIZE = "w780"
private const val POSTER_SIZE = "w342"
private const val PROFILE_SIZE = "w185"
private const val LOGO_SIZE = "w185"
private const val PATH_PREFIX = "/"
private const val PARENT_PATH = ".."
