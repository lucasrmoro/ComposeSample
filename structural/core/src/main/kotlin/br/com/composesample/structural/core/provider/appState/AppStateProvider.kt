package br.com.composesample.structural.core.provider.appState

import androidx.lifecycle.LiveData

interface AppStateProvider {

    val state: LiveData<AppState>

}
