package br.com.composesample.structural.session

import kotlinx.coroutines.flow.StateFlow

/** Tracks session expiration for the application lifetime. */
interface AppSessionProvider {

    /** Starts false and becomes true immediately when [expireSession] is called. */
    val isSessionExpired: StateFlow<Boolean>

    /** Marks the session expired; repeated calls preserve the expired state. */
    fun expireSession()

}
