package br.com.composesample.structural.core.provider.appState

import kotlinx.coroutines.flow.Flow

/** Combines session expiration and default-network validation for application consumers. */
interface AppStateProvider {

    /**
     * Emits the latest session and connectivity values when collected, then their updates.
     * Collection starts no background work beyond observing the underlying state flows.
     */
    val state: Flow<AppState>

}
