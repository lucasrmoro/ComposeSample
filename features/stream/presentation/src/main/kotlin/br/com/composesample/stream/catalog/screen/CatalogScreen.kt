package br.com.composesample.stream.catalog.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import br.com.composesample.structural.designsystem.components.SampleChip
import br.com.composesample.structural.designsystem.components.SampleLink
import br.com.composesample.structural.designsystem.components.SampleSearchBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogContent(state: CatalogUiState, event: (CatalogUiEvent) -> Unit) {
    Column(Modifier.fillMaxSize().navigationBarsPadding()) {
        Column(
            Modifier.padding(
                horizontal = CurrentTheme.spacing.large,
                vertical = CurrentTheme.spacing.small
            )
        ) {
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                MediaType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = state.type == type,
                        onClick = { event(CatalogUiEvent.SelectType(type = type)) },
                        shape = SegmentedButtonDefaults.itemShape(index, MediaType.entries.size),
                        icon = {}
                    ) {
                        Text(
                            stringResource(
                                if (type == MediaType.MOVIE) R.string.stream_movies
                                else R.string.stream_series
                            )
                        )
                    }
                }
            }
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
        if (state.searchText.isNotBlank()) {
            Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                Text(
                    stringResource(R.string.stream_results),
                    style = MaterialTheme.typography.titleMedium
                )
                LoadNotice(
                    state.search.refreshing,
                    state.search.page.items.isEmpty(),
                    state.search.error,
                    { event(CatalogUiEvent.SubmitSearch) }
                )
                if (state.search.error != null)
                    Text(
                        stringResource(R.string.stream_offline_search),
                        style = MaterialTheme.typography.bodySmall
                    )
            }
            MediaGrid(
                modifier = Modifier.weight(1f),
                page = state.search.page,
                kind = CollectionKind.SEARCH,
                paging = false,
                onDetails = { event(CatalogUiEvent.OpenDetails(id = it)) },
                onNext = { event(CatalogUiEvent.LoadMoreSearch) }
            )
        } else {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = CurrentTheme.spacing.large)
            ) {
                items(
                    listOf(CollectionKind.POPULAR, CollectionKind.RECENT, CollectionKind.UPCOMING)
                ) { kind ->
                    val section = state.sections[kind] ?: CatalogSection()
                    Column(Modifier.padding(vertical = CurrentTheme.spacing.small)) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = CurrentTheme.spacing.large),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(collectionTitle(kind), style = MaterialTheme.typography.titleLarge)
                            TextButton(
                                onClick = { event(CatalogUiEvent.OpenCollection(kind = kind)) }
                            ) {
                                Text(stringResource(R.string.stream_see_all))
                            }
                        }
                        Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                            LoadNotice(
                                section.refreshing,
                                section.page.items.isEmpty(),
                                section.error,
                                { event(CatalogUiEvent.Refresh) }
                            )
                        }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = CurrentTheme.spacing.large),
                            horizontalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.medium)
                        ) {
                            items(section.page.items, key = { "${it.id.type}:${it.id.value}" }) { item ->
                                SummaryCard(
                                    item = item,
                                    kind = kind,
                                    onClick = { event(CatalogUiEvent.OpenDetails(id = item.id)) }
                                )
                            }
                        }
                    }
                }
                item {
                    Column(Modifier.padding(CurrentTheme.spacing.large)) {
                        Text(
                            stringResource(R.string.stream_credits),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Image(
                            painter = painterResource(R.drawable.tmdb_logo),
                            contentDescription = stringResource(R.string.stream_tmdb_name),
                            modifier = Modifier.width(StreamDimens.portrait)
                        )
                        Text(
                            stringResource(R.string.stream_tmdb_attribution),
                            style = MaterialTheme.typography.labelSmall
                        )
                        SampleLink(
                            url = "https://www.themoviedb.org",
                            label = stringResource(R.string.stream_tmdb_name)
                        )
                    }
                }
                item {
                    TextButton(onClick = { event(CatalogUiEvent.Refresh) }) {
                        Text(stringResource(R.string.stream_refresh))
                    }
                }
            }
        }
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
