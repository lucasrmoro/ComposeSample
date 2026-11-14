package br.com.composesample.stream.details.viewModel

import androidx.lifecycle.viewModelScope
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.details.model.DetailsUiEffect
import br.com.composesample.stream.details.model.DetailsUiEvent
import br.com.composesample.stream.details.model.DetailsUiState
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.toCatalogRegion
import br.com.composesample.stream.useCase.ObserveMediaDetailsUseCase
import br.com.composesample.stream.useCase.ObserveSeasonUseCase
import br.com.composesample.stream.useCase.RefreshMediaDetailsUseCase
import br.com.composesample.stream.useCase.RefreshSeasonUseCase
import br.com.composesample.structural.core.base.viewModel.BaseViewModel
import br.com.composesample.structural.core.ext.onError
import br.com.composesample.structural.core.ext.onSuccess
import br.com.composesample.structural.core.text.UiText
import br.com.composesample.structural.localization.AppRegionProvider
import br.com.composesample.structural.network.connectivity.ConnectivityStatus
import br.com.composesample.structural.network.connectivity.NetworkConnectivityProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val mediaId: MediaId,
    private val observeMediaDetailsUseCase: ObserveMediaDetailsUseCase,
    private val refreshMediaDetailsUseCase: RefreshMediaDetailsUseCase,
    private val observeSeasonUseCase: ObserveSeasonUseCase,
    private val refreshSeasonUseCase: RefreshSeasonUseCase,
    private val appRegionProvider: AppRegionProvider,
    private val networkConnectivityProvider: NetworkConnectivityProvider
) : BaseViewModel<DetailsUiState, DetailsUiEvent, DetailsUiEffect>() {

    private val state = MutableStateFlow(DetailsUiState())
    override val uiState = state
        .onStart {
            viewModelScope.launch {
                bind()
                networkConnectivityProvider.connectivity.collect { status ->
                    state.update { it.copy(online = status == ConnectivityStatus.ONLINE) }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = DetailsUiState()
        )
    private var job: Job? = null
    private var seasonJob: Job? = null

    override fun sendEvent(event: DetailsUiEvent) {
        when (event) {
            is DetailsUiEvent.SelectSeason -> loadSeason(event.number)
            DetailsUiEvent.Retry ->
                state.value.selectedSeason
                    ?.takeIf { state.value.seasonError != null }
                    ?.let(::loadSeason) ?: bind()
        }
    }

    private fun bind() {
        job?.cancel()
        job = viewModelScope.launch {
            appRegionProvider.region.collectLatest { region ->
                seasonJob?.cancel()
                state.update {
                    it.copy(
                        loading = true,
                        error = null,
                        selectedSeason = null,
                        season = null,
                        seasonError = null
                    )
                }
                coroutineScope {
                    launch {
                        observeMediaDetailsUseCase(mediaId, region.toCatalogRegion())
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
                        refreshMediaDetailsUseCase(mediaId, region.toCatalogRegion())
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
        state.update {
            it.copy(
                selectedSeason = number,
                seasonLoading = true,
                season = null,
                seasonError = null
            )
        }
        val region = appRegionProvider.region.value.toCatalogRegion()
        seasonJob = viewModelScope.launch {
            coroutineScope {
                launch {
                    observeSeasonUseCase(mediaId, number, region)
                        .onError(ignoreUnauthorized = false) {
                            state.update {
                                it.copy(seasonError = UiText.StringResource(R.string.stream_load_error))
                            }
                        }
                        .onSuccess {
                            val season = this
                            state.update { it.copy(season = season, seasonError = null) }
                        }
                }
                launch {
                    refreshSeasonUseCase(mediaId, number, region)
                        .onCompletion {
                            if (currentCoroutineContext().isActive)
                                state.update { it.copy(seasonLoading = false) }
                        }
                        .onError(ignoreUnauthorized = false) {
                            state.update {
                                it.copy(seasonError = UiText.StringResource(R.string.stream_load_error))
                            }
                        }
                        .onSuccess {}
                }
            }
        }
    }

    private fun fail() {
        state.update { it.copy(error = UiText.StringResource(R.string.stream_load_error)) }
    }

}
