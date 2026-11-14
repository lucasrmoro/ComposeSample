package br.com.composesample.stream.viewModel.catalog

import androidx.lifecycle.viewModelScope
import br.com.composesample.features.stream.presentation.R
import br.com.composesample.stream.analytics.StreamAnalytics
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.CatalogUiEffect
import br.com.composesample.stream.model.catalog.CatalogUiEvent
import br.com.composesample.stream.model.catalog.CatalogUiState
import br.com.composesample.stream.model.catalog.CatalogUiState.CatalogSection
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.navigation.StreamDestination
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val observeCatalogUseCase: ObserveCatalogUseCase,
    private val refreshCatalogUseCase: RefreshCatalogUseCase,
    private val loadNextPageUseCase: LoadNextPageUseCase,
    private val appRegionProvider: AppRegionProvider,
    private val streamAnalytics: StreamAnalytics
) : BaseViewModel<CatalogUiState, CatalogUiEvent, CatalogUiEffect>() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    override val uiState = _uiState.asStateFlow()
        .onStart {
            viewModelScope.launch {
                appRegionProvider.region.collect {
                    bindFeed()
                    search(immediate = false)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = CatalogUiState()
        )
    private var feedJob: Job? = null
    private var searchJob: Job? = null
    private var pagingJob: Job? = null
    private val error
        get() = UiText.StringResource(R.string.stream_load_error)

    override fun sendEvent(event: CatalogUiEvent) {
        when (event) {
            is CatalogUiEvent.SelectType -> {
                if (_uiState.value.type != event.type) {
                    _uiState.update {
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
                _uiState.update { it.copy(searchText = event.text) }
                search(false)
            }
            CatalogUiEvent.SubmitSearch -> search(true)
            CatalogUiEvent.OpenGenres ->
                sendEffect(
                    CatalogUiEffect.Navigate(
                        destination = StreamDestination.Genres(type = _uiState.value.type)
                    )
                )
            is CatalogUiEvent.OpenCollection ->
                sendEffect(
                    CatalogUiEffect.Navigate(
                        destination =
                            StreamDestination.Collection(type = _uiState.value.type, kind = event.kind)
                    )
                )
            is CatalogUiEvent.OpenDetails ->
                sendEffect(
                    CatalogUiEffect.Navigate(
                        destination = StreamDestination.Details(id = event.id)
                    )
                )
            CatalogUiEvent.Refresh ->
                if (_uiState.value.searchText.isBlank()) bindFeed() else search(true)
            CatalogUiEvent.LoadMoreSearch ->
                if (
                    pagingJob?.isActive != true &&
                        !_uiState.value.search.refreshing &&
                        _uiState.value.search.error == null &&
                        _uiState.value.search.page.hasNextPage
                ) {
                    val query = query(CollectionKind.SEARCH)
                    pagingJob = viewModelScope.launch {
                        loadNextPageUseCase(query)
                            .onError(ignoreUnauthorized = false) {
                                _uiState.update { it.copy(search = it.search.copy(error = error)) }
                            }
                            .onSuccess {}
                    }
                }
        }
    }

    private fun query(kind: CollectionKind) =
        CatalogQuery(
            type = _uiState.value.type,
            kind = kind,
            region = appRegionProvider.region.value,
            searchText = if (kind == CollectionKind.SEARCH) _uiState.value.searchText.trim() else ""
        )

    private fun bindFeed() {
        streamAnalytics.listLoadStarted()
        feedJob?.cancel()
        feedJob = viewModelScope.launch {
            listOf(CollectionKind.POPULAR, CollectionKind.RECENT, CollectionKind.UPCOMING)
                .forEach { kind ->
                    val query = query(kind)
                    _uiState.update {
                        it.copy(
                            sections = it.sections +
                                (kind to (it.sections[kind] ?: CatalogSection()).copy(
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
        _uiState.update {
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
            _uiState.update { it.copy(search = CatalogSection(refreshing = false)) }
            return
        }
        _uiState.update { it.copy(search = CatalogSection(refreshing = true)) }
        searchJob = viewModelScope.launch {
            if (!immediate) delay(800)
            coroutineScope {
                launch {
                    observeCatalogUseCase(query)
                        .onError(ignoreUnauthorized = false) {
                            _uiState.update { it.copy(search = it.search.copy(error = error)) }
                        }
                        .onSuccess {
                            val page = this
                            _uiState.update { it.copy(search = it.search.copy(page = page)) }
                        }
                }
                launch {
                    refreshCatalogUseCase(query)
                        .onCompletion {
                            if (currentCoroutineContext().isActive)
                                _uiState.update {
                                    it.copy(search = it.search.copy(refreshing = false))
                                }
                        }
                        .onError(ignoreUnauthorized = false) {
                            _uiState.update { it.copy(search = it.search.copy(error = error)) }
                        }
                        .onSuccess {}
                }
            }
        }
    }

}
