package br.com.composesample.stream.collection.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.stream.collection.model.CollectionUiEffect
import br.com.composesample.stream.collection.model.CollectionUiEvent
import br.com.composesample.stream.collection.model.CollectionUiState
import br.com.composesample.stream.collection.viewModel.CollectionViewModel
import br.com.composesample.stream.components.LoadNotice
import br.com.composesample.stream.components.MediaGrid
import br.com.composesample.stream.components.collectionTitle
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.tokens.StreamDimens
import br.com.composesample.structural.designsystem.components.SampleScreen
import br.com.composesample.structural.designsystem.screen.BaseScreen
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

internal class CollectionScreen(
    private val collectionViewModel: CollectionViewModel,
    private val type: MediaType,
    private val kind: CollectionKind,
    private val genreId: Int?,
    private val title: String?,
    private val back: () -> Unit,
    private val onNavigate: (CollectionUiEffect) -> Unit
) : BaseScreen<CollectionUiState, CollectionUiEffect>(collectionViewModel) {

    @Composable
    override fun Content(uiState: CollectionUiState) {
        LaunchedEffect(type, kind, genreId) {
            collectionViewModel.sendEvent(CollectionUiEvent.Start(type, kind, genreId))
        }
        CollectionContent(
            uiState,
            title ?: collectionTitle(kind),
            back,
            collectionViewModel::sendEvent
        )
    }

    override fun consumeEffect(effect: CollectionUiEffect) = onNavigate(effect)

}

@Composable
private fun CollectionContent(
    state: CollectionUiState,
    title: String,
    back: () -> Unit,
    event: (CollectionUiEvent) -> Unit
) {
    SampleScreen(title = title, canNavigateBack = true, onNavigateBack = back) {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.padding(horizontal = CurrentTheme.spacing.large)) {
                LoadNotice(
                    loading = state.loading,
                    empty = state.page.items.isEmpty(),
                    error = state.error,
                    retry = { event(CollectionUiEvent.Retry) }
                )
            }
            MediaGrid(
                modifier = Modifier.weight(1f),
                page = state.page,
                kind = state.query?.kind ?: CollectionKind.POPULAR,
                paging = state.paging,
                onDetails = { event(CollectionUiEvent.OpenDetails(id = it)) },
                onNext = { event(CollectionUiEvent.NextPage) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionPreview() {
    ComposeSampleTheme {
        CollectionContent(
            CollectionUiState(loading = false),
            "Popular",
            {},
            {}
        )
    }
}
