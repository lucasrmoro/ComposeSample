package br.com.composesample.stream.collection.screen

import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaType
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.stream.collection.model.*
import br.com.composesample.stream.collection.viewModel.CollectionViewModel
import br.com.composesample.stream.components.*
import br.com.composesample.structural.designsystem.components.SampleScreen
import br.com.composesample.structural.designsystem.screen.BaseScreen
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.stream.tokens.StreamDimens

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
                    state.loading,
                    state.page.items.isEmpty(),
                    state.error,
                    { event(CollectionUiEvent.Retry) }
                )
            }
            MediaGrid(
                state.page,
                state.query?.kind ?: CollectionKind.POPULAR,
                state.paging,
                { event(CollectionUiEvent.OpenDetails(id = it)) },
                { event(CollectionUiEvent.NextPage) },
                Modifier.weight(1f)
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
