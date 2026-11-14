package br.com.composesample.stream.genres.viewModel

import androidx.lifecycle.viewModelScope
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.genres.model.GenresUiEffect
import br.com.composesample.stream.genres.model.GenresUiEvent
import br.com.composesample.stream.genres.model.GenresUiState
import br.com.composesample.stream.model.toCatalogRegion
import br.com.composesample.stream.useCase.ObserveGenresUseCase
import br.com.composesample.stream.useCase.RefreshGenresUseCase
import br.com.composesample.structural.core.base.viewModel.BaseViewModel
import br.com.composesample.structural.core.ext.onError
import br.com.composesample.structural.core.ext.onSuccess
import br.com.composesample.structural.core.text.UiText
import br.com.composesample.structural.localization.AppRegionProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GenresViewModel(
    private val observeGenresUseCase: ObserveGenresUseCase,
    private val refreshGenresUseCase: RefreshGenresUseCase,
    private val appRegionProvider: AppRegionProvider
) : BaseViewModel<GenresUiState, GenresUiEvent, GenresUiEffect>() {

    private val state = MutableStateFlow(GenresUiState())
    override val uiState = state.asStateFlow()
    private var job: Job? = null

    override fun sendEvent(event: GenresUiEvent) {
        when (event) {
            is GenresUiEvent.Start ->
                if (job == null) {
                    state.update { it.copy(type = event.type) }
                    bind()
                }
            is GenresUiEvent.Select ->
                sendEffect(GenresUiEffect.Collection(state.value.type, event.genre))
            GenresUiEvent.Retry -> bind()
        }
    }

    private fun bind() {
        job?.cancel()
        job = viewModelScope.launch {
            appRegionProvider.region.collectLatest { region ->
                state.update { it.copy(loading = true, error = null) }
                coroutineScope {
                    launch {
                        observeGenresUseCase(state.value.type, region.toCatalogRegion())
                            .onError(ignoreUnauthorized = false) { fail() }
                            .onSuccess {
                                val genres = this
                                state.update { it.copy(genres = genres) }
                            }
                    }
                    launch {
                        refreshGenresUseCase(state.value.type, region.toCatalogRegion())
                            .onCompletion {
                                if (currentCoroutineContext().isActive)
                                    state.update { it.copy(loading = false) }
                            }
                            .onError(ignoreUnauthorized = false) { fail() }
                            .onSuccess {}
                    }
                }
            }
        }
    }

    private fun fail() {
        state.update { it.copy(error = UiText.StringResource(R.string.stream_load_error)) }
    }

}
