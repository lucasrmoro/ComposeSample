package br.com.composesample.stream.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.model.catalog.CollectionKind

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
