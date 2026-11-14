package br.com.composesample.stream.ui.screen.genres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.ui.components.LoadNotice
import br.com.composesample.stream.model.genres.GenresUiEffect
import br.com.composesample.stream.model.genres.GenresUiEvent
import br.com.composesample.stream.model.genres.GenresUiState
import br.com.composesample.stream.viewModel.genres.GenresViewModel
import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.tokens.StreamDimens
import br.com.composesample.structural.designsystem.components.SampleScreen
import br.com.composesample.structural.designsystem.components.SampleSurface
import br.com.composesample.structural.designsystem.components.SampleText
import br.com.composesample.structural.designsystem.screen.BaseScreen
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.designsystem.theme.CurrentTheme

internal class GenresScreen(
    private val genresViewModel: GenresViewModel,
    private val back: () -> Unit,
    private val onNavigate: (GenresUiEffect) -> Unit
) : BaseScreen<GenresUiState, GenresUiEffect>(genresViewModel) {

    @Composable
    override fun Content(uiState: GenresUiState) {
        GenresContent(uiState, back, genresViewModel::sendEvent)
    }

    override fun consumeEffect(effect: GenresUiEffect) = onNavigate(effect)

}

@Composable
private fun GenresContent(state: GenresUiState, back: () -> Unit, event: (GenresUiEvent) -> Unit) {
    SampleScreen(
        title = stringResource(R.string.stream_genres),
        canNavigateBack = true,
        onNavigateBack = back
    ) {
        LazyColumn(
            contentPadding = PaddingValues(CurrentTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(CurrentTheme.spacing.small)
        ) {
            item {
                LoadNotice(
                    loading = state.loading,
                    empty = state.genres.isEmpty(),
                    error = state.error,
                    retry = { event(GenresUiEvent.Retry) }
                )
            }
            items(state.genres, key = Genre::id) { genre ->
                SampleSurface(
                    modifier = Modifier.fillMaxWidth().heightIn(min = CurrentTheme.size.xxxLarge),
                    onClick = { event(GenresUiEvent.Select(genre = genre)) },
                    outlined = true
                ) {
                    SampleText(
                        modifier = Modifier.padding(CurrentTheme.spacing.large),
                        text = genre.name
                    )
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
