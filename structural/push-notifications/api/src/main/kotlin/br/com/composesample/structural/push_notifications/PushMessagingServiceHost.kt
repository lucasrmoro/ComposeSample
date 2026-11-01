package br.com.composesample.structural.push_notifications

import br.com.composesample.structural.logger.Logger

/** Provides app-scoped dependencies to the Android-created Firebase messaging service. */
interface PushMessagingServiceHost {

    /** Returns the handler registered in the application's dependency container. */
    fun resolvePushMessageHandler(): PushMessageHandler

    /** Returns the logger registered in the application's dependency container. */
    fun resolveLogger(): Logger

}
