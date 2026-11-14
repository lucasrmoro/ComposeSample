package br.com.composesample.stream.collection.viewModel

import androidx.lifecycle.viewModelScope
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.collection.model.CollectionUiEffect
import br.com.composesample.stream.collection.model.CollectionUiEvent
import br.com.composesample.stream.collection.model.CollectionUiState
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.ui.toCatalogRegion
import br.com.composesample.stream.useCase.LoadNextPageUseCase
import br.com.composesample.stream.useCase.ObserveCatalogUseCase
import br.com.composesample.stream.useCase.RefreshCatalogUseCase
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CollectionViewModel(
    type: MediaType,
    kind: CollectionKind,
    genreId: Int?,
    private val observeCatalogUseCase: ObserveCatalogUseCase,
    private val refreshCatalogUseCase: RefreshCatalogUseCase,
    private val loadNextPageUseCase: LoadNextPageUseCase,
    private val appRegionProvider: AppRegionProvider
) : BaseViewModel<CollectionUiState, CollectionUiEvent, CollectionUiEffect>() {

    private val initialQuery = CatalogQuery(
        type = type,
        kind = kind,
        region = appRegionProvider.region.value.toCatalogRegion(),
        genreId = genreId
    )
    private val initialState = CollectionUiState(query = initialQuery)
    private val state = MutableStateFlow(initialState)
    override val uiState = state.asStateFlow()
        .onStart { viewModelScope.launch { bind() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = initialState
        )
    private var job: Job? = null
    private var pageJob: Job? = null

    override fun sendEvent(event: CollectionUiEvent) {
        when (event) {
            is CollectionUiEvent.OpenDetails -> sendEffect(CollectionUiEffect.Details(event.id))
            CollectionUiEvent.Retry -> bind()
            CollectionUiEvent.NextPage ->
                if (
                    pageJob?.isActive != true &&
                        !state.value.loading &&
                        state.value.error == null &&
                        state.value.page.hasNextPage
                ) {
                    val query = state.value.query
                    state.update { it.copy(paging = true, error = null) }
                    pageJob = viewModelScope.launch {
                        loadNextPageUseCase(query)
                            .onCompletion { state.update { it.copy(paging = false) } }
                            .onError(ignoreUnauthorized = false) { fail() }
                            .onSuccess {}
                    }
                }
        }
    }

    private fun bind() {
        job?.cancel()
        pageJob?.cancel()
        job = viewModelScope.launch {
            appRegionProvider.region.collectLatest { region ->
                pageJob?.cancel()
                pageJob = null
                val query = initialQuery.copy(region = region.toCatalogRegion())
                state.update { it.copy(query = query, loading = true, paging = false, error = null) }
                coroutineScope {
                    launch {
                        observeCatalogUseCase(query)
                            .onError(ignoreUnauthorized = false) { fail() }
                            .onSuccess {
                                val page = this
                                state.update { it.copy(page = page) }
                            }
                    }
                    launch {
                        refreshCatalogUseCase(query)
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
