package br.com.composesample.stream.collection.viewModel

import br.com.composesample.stream.model.toCatalogRegion
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.useCase.LoadNextPageUseCase
import br.com.composesample.stream.useCase.ObserveCatalogUseCase
import br.com.composesample.stream.useCase.RefreshCatalogUseCase
import androidx.lifecycle.viewModelScope
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.collection.model.*
import br.com.composesample.structural.core.base.viewModel.BaseViewModel
import br.com.composesample.structural.core.ext.onError
import br.com.composesample.structural.core.ext.onSuccess
import br.com.composesample.structural.core.text.UiText
import br.com.composesample.structural.localization.AppRegionProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CollectionViewModel(
    private val observeCatalogUseCase: ObserveCatalogUseCase,
    private val refreshCatalogUseCase: RefreshCatalogUseCase,
    private val loadNextPageUseCase: LoadNextPageUseCase,
    private val appRegionProvider: AppRegionProvider
) : BaseViewModel<CollectionUiState, CollectionUiEvent, CollectionUiEffect>() {

    private val state = MutableStateFlow(CollectionUiState())

    override val uiState = state.asStateFlow()

    private var job: Job? = null

    private var pageJob: Job? = null

    override fun sendEvent(event: CollectionUiEvent) {
        when (event) {
            is CollectionUiEvent.Start ->
                if (job == null) {
                    state.update {
                        it.copy(
                            query =
                                CatalogQuery(
                                    type = event.type,
                                    kind = event.kind,
                                    region = appRegionProvider.region.value.toCatalogRegion(),
                                    genreId = event.genreId
                                )
                        )
                    }
                    bind()
                }
            is CollectionUiEvent.OpenDetails -> sendEffect(CollectionUiEffect.Details(event.id))
            CollectionUiEvent.Retry -> bind()
            CollectionUiEvent.NextPage ->
                if (
                    pageJob?.isActive != true &&
                        !state.value.loading &&
                        state.value.error == null &&
                        state.value.page.hasNextPage
                ) {
                    val query = state.value.query ?: return
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
                val query =
                    requireNotNull(state.value.query).copy(region = region.toCatalogRegion())
                state.update { it.copy(query = query, loading = true, error = null) }
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
