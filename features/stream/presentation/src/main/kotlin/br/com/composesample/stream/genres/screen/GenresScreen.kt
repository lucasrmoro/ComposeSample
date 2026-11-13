package br.com.composesample.stream.genres.screen

import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.media.MediaType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.components.LoadNotice
import br.com.composesample.stream.genres.model.*
import br.com.composesample.stream.genres.viewModel.GenresViewModel
import br.com.composesample.structural.designsystem.components.SampleScreen
import br.com.composesample.structural.designsystem.screen.BaseScreen
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.stream.tokens.StreamDimens

internal class GenresScreen(
    private val genresViewModel: GenresViewModel,
    private val type: MediaType,
    private val back: () -> Unit,
    private val onNavigate: (GenresUiEffect) -> Unit
) : BaseScreen<GenresUiState, GenresUiEffect>(genresViewModel) {

    @Composable
    override fun Content(uiState: GenresUiState) {
        LaunchedEffect(type) { genresViewModel.sendEvent(GenresUiEvent.Start(type)) }
        GenresContent(uiState, back, genresViewModel::sendEvent)
    }

    override fun consumeEffect(effect: GenresUiEffect) = onNavigate(effect)

}

@Composable
private fun GenresContent(state: GenresUiState, back: () -> Unit, event: (GenresUiEvent) -> Unit) {
    SampleScreen(title = stringResource(R.string.stream_genres), canNavigateBack = true, onNavigateBack = back) {
        LazyColumn(
            contentPadding = PaddingValues(CurrentTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small)
        ) {
            item {
                LoadNotice(
                    state.loading,
                    state.genres.isEmpty(),
                    state.error,
                    { event(GenresUiEvent.Retry) }
                )
            }
            items(state.genres, key = Genre::id) { genre ->
                OutlinedCard(
                    onClick = { event(GenresUiEvent.Select(genre = genre)) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = CurrentTheme.size.xxxLarge)
                ) {
                    Text(genre.name, Modifier.padding(CurrentTheme.spacing.large))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenresPreview() {
    ComposeSampleTheme { GenresContent(GenresUiState(loading = false), {}, {}) }
}
