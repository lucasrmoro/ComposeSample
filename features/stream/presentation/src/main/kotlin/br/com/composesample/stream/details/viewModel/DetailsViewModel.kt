package br.com.composesample.stream.details.viewModel

import br.com.composesample.stream.model.toCatalogRegion
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.useCase.ObserveMediaDetailsUseCase
import br.com.composesample.stream.useCase.ObserveSeasonUseCase
import br.com.composesample.stream.useCase.RefreshMediaDetailsUseCase
import br.com.composesample.stream.useCase.RefreshSeasonUseCase
import androidx.lifecycle.viewModelScope
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.details.model.*
import br.com.composesample.structural.core.base.viewModel.BaseViewModel
import br.com.composesample.structural.core.ext.onError
import br.com.composesample.structural.core.ext.onSuccess
import br.com.composesample.structural.core.text.UiText
import br.com.composesample.structural.localization.AppRegionProvider
import br.com.composesample.structural.network.connectivity.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class DetailsViewModel(
    private val observeMediaDetailsUseCase: ObserveMediaDetailsUseCase,
    private val refreshMediaDetailsUseCase: RefreshMediaDetailsUseCase,
    private val observeSeasonUseCase: ObserveSeasonUseCase,
    private val refreshSeasonUseCase: RefreshSeasonUseCase,
    private val appRegionProvider: AppRegionProvider,
    private val networkConnectivityProvider: NetworkConnectivityProvider
) : BaseViewModel<DetailsUiState, DetailsUiEvent, DetailsUiEffect>() {

    private val state = MutableStateFlow(DetailsUiState())

    override val uiState = state.asStateFlow()

    private var id: MediaId? = null

    private var job: Job? = null

    private var seasonJob: Job? = null

    override fun sendEvent(event: DetailsUiEvent) {
        when (event) {
            is DetailsUiEvent.Start ->
                if (id == null) {
                    id = event.id
                    bind()
                    launch {
                        networkConnectivityProvider.connectivity.collect { status ->
                            state.update { it.copy(online = status == ConnectivityStatus.ONLINE) }
                        }
                    }
                }
            is DetailsUiEvent.SelectSeason -> loadSeason(event.number)
            DetailsUiEvent.Retry -> bind()
        }
    }

    private fun bind() {
        job?.cancel()
        job = viewModelScope.launch {
            appRegionProvider.region.collectLatest { region ->
                seasonJob?.cancel()
                state.update {
                    it.copy(loading = true, error = null, selectedSeason = null, season = null)
                }
                coroutineScope {
                    launch {
                        observeMediaDetailsUseCase(requireNotNull(id), region.toCatalogRegion())
                            .onError(ignoreUnauthorized = false) { fail() }
                            .onSuccess {
                                val details = this
                                state.update { it.copy(details = details) }
                                details?.seasons?.firstOrNull()?.let {
                                    if (state.value.selectedSeason == null) loadSeason(it.number)
                                }
                            }
                    }
                    launch {
                        refreshMediaDetailsUseCase(requireNotNull(id), region.toCatalogRegion())
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

    private fun loadSeason(number: Int) {
        seasonJob?.cancel()
        state.update { it.copy(selectedSeason = number, seasonLoading = true, season = null) }
        val region = appRegionProvider.region.value.toCatalogRegion()
        seasonJob = viewModelScope.launch {
            coroutineScope {
                launch {
                    observeSeasonUseCase(requireNotNull(id), number, region)
                        .onError(ignoreUnauthorized = false) { fail() }
                        .onSuccess {
                            val season = this
                            state.update { it.copy(season = season) }
                        }
                }
                launch {
                    refreshSeasonUseCase(requireNotNull(id), number, region)
                        .onCompletion { state.update { it.copy(seasonLoading = false) } }
                        .onError(ignoreUnauthorized = false) { fail() }
                        .onSuccess {}
                }
            }
        }
    }

    private fun fail() {
        state.update { it.copy(error = UiText.StringResource(R.string.stream_load_error)) }
    }

}
