package br.com.composesample.stream.catalog.viewModel

import br.com.composesample.stream.model.toCatalogRegion
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.useCase.LoadNextPageUseCase
import br.com.composesample.stream.useCase.ObserveCatalogUseCase
import br.com.composesample.stream.useCase.RefreshCatalogUseCase
import androidx.lifecycle.viewModelScope
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.analytics.StreamAnalytics
import br.com.composesample.stream.catalog.model.*
import br.com.composesample.structural.core.base.viewModel.BaseViewModel
import br.com.composesample.structural.core.ext.onError
import br.com.composesample.structural.core.ext.onSuccess
import br.com.composesample.structural.core.text.UiText
import br.com.composesample.structural.localization.AppRegionProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CatalogViewModel(
    private val observeCatalogUseCase: ObserveCatalogUseCase,
    private val refreshCatalogUseCase: RefreshCatalogUseCase,
    private val loadNextPageUseCase: LoadNextPageUseCase,
    private val appRegionProvider: AppRegionProvider,
    private val streamAnalytics: StreamAnalytics
) : BaseViewModel<CatalogUiState, CatalogUiEvent, CatalogUiEffect>() {

    private val state = MutableStateFlow(CatalogUiState())

    override val uiState = state.asStateFlow()

    private var started = false

    private var feedJob: Job? = null

    private var searchJob: Job? = null

    private var pagingJob: Job? = null

    private val error
        get() = UiText.StringResource(R.string.stream_load_error)

    override fun sendEvent(event: CatalogUiEvent) {
        when (event) {
            CatalogUiEvent.Start ->
                if (!started) {
                    started = true
                    launch {
                        appRegionProvider.region.collect {
                            bindFeed()
                            search(false)
                        }
                    }
                }
            is CatalogUiEvent.SelectType -> {
                if (state.value.type != event.type) {
                    state.update {
                        it.copy(
                            type = event.type,
                            sections = emptyMap(),
                            search = CatalogSection(refreshing = false)
                        )
                    }
                    bindFeed()
                    search(false)
                }
            }
            is CatalogUiEvent.SearchChanged -> {
                state.update { it.copy(searchText = event.text) }
                search(false)
            }
            CatalogUiEvent.SubmitSearch -> search(true)
            CatalogUiEvent.OpenGenres -> sendEffect(CatalogUiEffect.Genres(state.value.type))
            is CatalogUiEvent.OpenCollection ->
                sendEffect(CatalogUiEffect.Collection(type = state.value.type, kind = event.kind))
            is CatalogUiEvent.OpenDetails -> sendEffect(CatalogUiEffect.Details(id = event.id))
            CatalogUiEvent.Refresh ->
                if (state.value.searchText.isBlank()) bindFeed() else search(true)
            CatalogUiEvent.LoadMoreSearch ->
                if (
                    pagingJob?.isActive != true &&
                        !state.value.search.refreshing &&
                        state.value.search.error == null &&
                        state.value.search.page.hasNextPage
                ) {
                    val query = query(CollectionKind.SEARCH)
                    pagingJob = viewModelScope.launch {
                        loadNextPageUseCase(query)
                            .onError(ignoreUnauthorized = false) {
                                state.update { it.copy(search = it.search.copy(error = error)) }
                            }
                            .onSuccess {}
                    }
                }
        }
    }

    private fun query(kind: CollectionKind) =
        CatalogQuery(
            type = state.value.type,
            kind = kind,
            region = appRegionProvider.region.value.toCatalogRegion(),
            searchText = if (kind == CollectionKind.SEARCH) state.value.searchText.trim() else ""
        )

    private fun bindFeed() {
        streamAnalytics.listLoadStarted()
        feedJob?.cancel()
        feedJob = viewModelScope.launch {
            listOf(CollectionKind.POPULAR, CollectionKind.RECENT, CollectionKind.UPCOMING)
                .forEach { kind ->
                    val query = query(kind)
                    state.update {
                        it.copy(
                            sections =
                                it.sections +
                                    (kind to
                                        (it.sections[kind] ?: CatalogSection()).copy(
                                            refreshing = true,
                                            error = null
                                        ))
                        )
                    }
                    launch {
                        observeCatalogUseCase(query)
                            .onError(ignoreUnauthorized = false) {
                                updateSection(kind) { it.copy(error = error) }
                            }
                            .onSuccess {
                                val page = this
                                if (kind == CollectionKind.POPULAR && page.items.isNotEmpty())
                                    streamAnalytics.listLoadFinished(page.items.size)
                                updateSection(kind) { it.copy(page = page) }
                            }
                    }
                    launch {
                        refreshCatalogUseCase(query)
                            .onCompletion { updateSection(kind) { it.copy(refreshing = false) } }
                            .onError(ignoreUnauthorized = false) {
                                updateSection(kind) { it.copy(error = error) }
                            }
                            .onSuccess {}
                    }
                }
        }
    }

    private fun updateSection(kind: CollectionKind, transform: (CatalogSection) -> CatalogSection) {
        state.update {
            it.copy(
                sections = it.sections + (kind to transform(it.sections[kind] ?: CatalogSection()))
            )
        }
    }

    private fun search(immediate: Boolean) {
        searchJob?.cancel()
        pagingJob?.cancel()
        val query = query(CollectionKind.SEARCH)
        if (query.searchText.isBlank()) {
            state.update { it.copy(search = CatalogSection(refreshing = false)) }
            return
        }
        state.update { it.copy(search = CatalogSection(refreshing = true)) }
        searchJob = viewModelScope.launch {
            if (!immediate) delay(800)
            coroutineScope {
                launch {
                    observeCatalogUseCase(query)
                        .onError(ignoreUnauthorized = false) {
                            state.update { it.copy(search = it.search.copy(error = error)) }
                        }
                        .onSuccess {
                            val page = this
                            state.update { it.copy(search = it.search.copy(page = page)) }
                        }
                }
                launch {
                    refreshCatalogUseCase(query)
                        .onCompletion {
                            if (currentCoroutineContext().isActive)
                                state.update {
                                    it.copy(search = it.search.copy(refreshing = false))
                                }
                        }
                        .onError(ignoreUnauthorized = false) {
                            state.update { it.copy(search = it.search.copy(error = error)) }
                        }
                        .onSuccess {}
                }
            }
        }
    }

}
