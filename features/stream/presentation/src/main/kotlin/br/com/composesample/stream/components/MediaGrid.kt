package br.com.composesample.stream.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaSummary
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.tokens.StreamDimens
import br.com.composesample.structural.designsystem.components.SampleButton
import br.com.composesample.structural.designsystem.components.SampleButtonStyle
import br.com.composesample.structural.designsystem.components.SampleCircularProgressIndicator
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

@Composable
internal fun MediaGrid(
    modifier: Modifier = Modifier,
    page: CatalogPage,
    kind: CollectionKind,
    paging: Boolean,
    onDetails: (MediaId) -> Unit,
    onNext: () -> Unit
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
        columns = GridCells.Fixed(2),
        modifier = modifier,
        state = scroll,
        horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.large),
        contentPadding = PaddingValues(CurrentTheme.spacing.large)
    ) {
        items(page.items, key = { "${it.id.type}:${it.id.value}" }) { item ->
            SummaryCard(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                kind = kind,
                onClick = { onDetails(item.id) }
            )
        }
        if (paging) item { SampleCircularProgressIndicator() }
        if (page.hasNextPage && !paging) {
            item {
                SampleButton(
                    text = stringResource(R.string.stream_more),
                    onClick = onNext,
                    style = SampleButtonStyle.TEXT
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaGridPreview() {
    ComposeSampleTheme {
        MediaGrid(
            modifier = Modifier.height(StreamDimens.posterHeight),
            page = CatalogPage(
                items = listOf(
                    MediaSummary(
                        id = MediaId(type = MediaType.MOVIE, value = 1),
                        title = "Hidden valley",
                        posterPath = null,
                        year = "2026",
                        rating = null,
                        certification = null,
                        releaseEvent = null
                    )
                )
            ),
            kind = CollectionKind.POPULAR,
            paging = false,
            onDetails = {},
            onNext = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaGridPagingPreview() {
    ComposeSampleTheme {
        MediaGrid(
            modifier = Modifier.height(StreamDimens.posterHeight),
            page = CatalogPage(),
            kind = CollectionKind.POPULAR,
            paging = true,
            onDetails = {},
            onNext = {}
        )
    }
}
