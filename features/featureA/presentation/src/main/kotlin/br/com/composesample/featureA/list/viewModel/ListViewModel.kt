package br.com.composesample.featureA.list.viewModel

import br.com.composesample.featureA.analytics.FeatureAAnalytics
import br.com.composesample.featureA.model.SomeModel
import br.com.composesample.featureA.list.model.ListUiEffect
import br.com.composesample.featureA.list.model.ListUiEvent
import br.com.composesample.featureA.list.model.ListUiState
import br.com.composesample.structural.core.base.viewModel.BaseViewModel
import br.com.composesample.structural.core.ext.ONE
import br.com.composesample.structural.core.ext.ONE_HUNDRED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ListViewModel(
    private val analytics: FeatureAAnalytics
) : BaseViewModel<ListUiState, ListUiEvent, ListUiEffect>() {

    private val _uiState = MutableStateFlow(ListUiState())
    override val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val itemsPerPage = Int.ONE_HUNDRED

    override fun sendEvent(event: ListUiEvent) {
        when (event) {
            ListUiEvent.LoadItems -> loadItems()
            ListUiEvent.AddItemClicked -> launch { sendEffect(ListUiEffect.NavigateToForm) }
        }
    }

    private fun loadItems() {
        if (_uiState.value.isLoading || _uiState.value.items.isNotEmpty()) return

        launch {
            _uiState.update { it.copy(isLoading = true) }
            analytics.listLoadStarted(Int.ONE)
            val items = (1..itemsPerPage).map { id ->
                SomeModel(id.toLong(), "Teste $id")
            }
            analytics.listLoadFinished(Int.ONE, itemCount = items.size)
            _uiState.update { it.copy(isLoading = false, items = items) }
        }
    }

}
