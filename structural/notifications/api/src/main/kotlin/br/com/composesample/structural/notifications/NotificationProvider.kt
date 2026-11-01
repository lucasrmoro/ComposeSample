package br.com.composesample.structural.notifications

/** Creates notification channels and displays local notifications. */
interface NotificationProvider {

    /** Ensures the request channel exists, then displays it or returns PERMISSION_REQUIRED if permission is absent. */
    fun show(request: NotificationRequest): NotificationResult

}
