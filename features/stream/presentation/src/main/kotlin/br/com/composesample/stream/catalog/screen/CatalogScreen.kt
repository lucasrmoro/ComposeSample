package br.com.composesample.stream.catalog.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.catalog.model.CatalogSection
import br.com.composesample.stream.catalog.model.CatalogUiEffect
import br.com.composesample.stream.catalog.model.CatalogUiEvent
import br.com.composesample.stream.catalog.model.CatalogUiState
import br.com.composesample.stream.catalog.viewModel.CatalogViewModel
import br.com.composesample.stream.components.LoadNotice
import br.com.composesample.stream.components.MediaGrid
import br.com.composesample.stream.components.SummaryCard
import br.com.composesample.stream.components.collectionTitle
import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaSummary
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.tokens.StreamDimens
import br.com.composesample.structural.designsystem.components.SampleButton
import br.com.composesample.structural.designsystem.components.SampleButtonStyle
import br.com.composesample.structural.designsystem.components.SampleChip
import br.com.composesample.structural.designsystem.components.SampleLink
import br.com.composesample.structural.designsystem.components.SampleSearchBar
import br.com.composesample.structural.designsystem.components.SampleSegmentedButtonRow
import br.com.composesample.structural.designsystem.components.SampleText
import br.com.composesample.structural.designsystem.screen.BaseScreen
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

internal class CatalogScreen(
    private val catalogViewModel: CatalogViewModel,
    private val onNavigate: (CatalogUiEffect) -> Unit
) : BaseScreen<CatalogUiState, CatalogUiEffect>(catalogViewModel) {

    @Composable
    override fun Content(uiState: CatalogUiState) {
        LaunchedEffect(Unit) { catalogViewModel.sendEvent(CatalogUiEvent.Start) }
        CatalogContent(uiState, catalogViewModel::sendEvent)
    }

    override fun consumeEffect(effect: CatalogUiEffect) = onNavigate(effect)

}

@Composable
private fun CatalogContent(state: CatalogUiState, event: (CatalogUiEvent) -> Unit) {
    Column(Modifier.fillMaxSize().navigationBarsPadding()) {
        CatalogHeader(state = state, event = event)
        if (state.searchText.isNotBlank()) {
            CatalogSearchContent(state = state, event = event)
        } else {
            CatalogSections(state = state, event = event)
        }
    }
}

@Composable
private fun CatalogHeader(state: CatalogUiState, event: (CatalogUiEvent) -> Unit) {
    Column(
        Modifier.padding(
            horizontal = CurrentTheme.spacing.large,
            vertical = CurrentTheme.spacing.small
        )
    ) {
        SampleSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
            options = listOf(
                stringResource(R.string.stream_movies),
                stringResource(R.string.stream_series)
            ),
            selectedIndex = MediaType.entries.indexOf(state.type),
            onOptionSelected = { index ->
                MediaType.entries.getOrNull(index)?.let { type ->
                    event(CatalogUiEvent.SelectType(type = type))
                }
            }
        )
        Spacer(Modifier.height(CurrentTheme.spacing.large))
        SampleSearchBar(
            value = state.searchText,
            placeholder = stringResource(
                if (state.type == MediaType.MOVIE) R.string.stream_search_movies
                else R.string.stream_search_series
            ),
            onValueChange = { event(CatalogUiEvent.SearchChanged(text = it)) },
            onSearch = { event(CatalogUiEvent.SubmitSearch) }
        )
        SampleChip(
            label = stringResource(R.string.stream_genres),
            onClick = { event(CatalogUiEvent.OpenGenres) }
        )
    }
}

@Composable
private fun ColumnScope.CatalogSearchContent(
    state: CatalogUiState,
    event: (CatalogUiEvent) -> Unit
) {
    Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
        SampleText(
            text = stringResource(R.string.stream_results),
            style = CurrentTheme.typography.bodyLarge
        )
        LoadNotice(
            loading = state.search.refreshing,
            empty = state.search.page.items.isEmpty(),
            error = state.search.error,
            retry = { event(CatalogUiEvent.SubmitSearch) }
        )
        state.search.error?.let {
            SampleText(
                text = stringResource(R.string.stream_offline_search),
                style = CurrentTheme.typography.labelSmall
            )
        }
    }
    MediaGrid(
        modifier = Modifier.weight(1f),
        page = state.search.page,
        kind = CollectionKind.SEARCH,
        paging = false,
        onDetails = { event(CatalogUiEvent.OpenDetails(id = it)) },
        onNext = { event(CatalogUiEvent.LoadMoreSearch) }
    )
}

@Composable
private fun ColumnScope.CatalogSections(state: CatalogUiState, event: (CatalogUiEvent) -> Unit) {
    LazyColumn(
        Modifier.weight(1f),
        contentPadding = PaddingValues(bottom = CurrentTheme.spacing.large)
    ) {
        items(
            listOf(CollectionKind.POPULAR, CollectionKind.RECENT, CollectionKind.UPCOMING)
        ) { kind ->
            CatalogCollectionSection(
                kind = kind,
                section = state.sections[kind] ?: CatalogSection(),
                event = event
            )
        }
        item { CatalogAttribution() }
        item {
            SampleButton(
                text = stringResource(R.string.stream_refresh),
                onClick = { event(CatalogUiEvent.Refresh) },
                style = SampleButtonStyle.TEXT
            )
        }
    }
}

@Composable
private fun CatalogCollectionSection(
    kind: CollectionKind,
    section: CatalogSection,
    event: (CatalogUiEvent) -> Unit
) {
    Column(Modifier.padding(vertical = CurrentTheme.spacing.small)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = CurrentTheme.spacing.large),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SampleText(
                text = collectionTitle(kind),
                style = CurrentTheme.typography.bodyLarge
            )
            SampleButton(
                text = stringResource(R.string.stream_see_all),
                onClick = { event(CatalogUiEvent.OpenCollection(kind = kind)) },
                style = SampleButtonStyle.TEXT
            )
        }
        Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
            LoadNotice(
                loading = section.refreshing,
                empty = section.page.items.isEmpty(),
                error = section.error,
                retry = { event(CatalogUiEvent.Refresh) }
            )
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = CurrentTheme.spacing.large),
            horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.medium)
        ) {
            items(section.page.items, key = { it.id.type.toString() + ":" + it.id.value }) { item ->
                SummaryCard(
                    item = item,
                    kind = kind,
                    onClick = { event(CatalogUiEvent.OpenDetails(id = item.id)) }
                )
            }
        }
    }
}

@Composable
private fun CatalogAttribution() {
    Column(Modifier.padding(CurrentTheme.spacing.large)) {
        SampleText(
            text = stringResource(R.string.stream_credits),
            style = CurrentTheme.typography.bodyLarge
        )
        Image(
            painter = painterResource(R.drawable.tmdb_logo),
            contentDescription = stringResource(R.string.stream_tmdb_name),
            modifier = Modifier.width(StreamDimens.portrait)
        )
        SampleText(
            text = stringResource(R.string.stream_tmdb_attribution),
            style = CurrentTheme.typography.labelSmall
        )
        SampleLink(
            url = "https://www.themoviedb.org",
            label = stringResource(R.string.stream_tmdb_name)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogPreview() {
    ComposeSampleTheme {
        CatalogContent(
            state = CatalogUiState(
                sections = mapOf(
                    CollectionKind.POPULAR to CatalogSection(
                        page = CatalogPage(
                            items = listOf(
                                MediaSummary(
                                    id = MediaId(type = MediaType.MOVIE, value = 1),
                                    title = "Beyond the horizon",
                                    posterPath = null,
                                    year = "2026",
                                    rating = 7.8,
                                    certification = "16",
                                    releaseEvent = null
                                )
                            )
                        ),
                        refreshing = false
                    )
                )
            ),
            event = {}
        )
    }
}
