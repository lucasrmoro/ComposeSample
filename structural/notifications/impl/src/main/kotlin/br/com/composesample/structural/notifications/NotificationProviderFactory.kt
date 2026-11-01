package br.com.composesample.structural.notifications

import android.content.Context
import br.com.composesample.structural.permissions.PermissionsProvider

fun createNotificationProvider(
    context: Context,
    permissionsProvider: PermissionsProvider
): NotificationProvider = NotificationProviderImpl(context.applicationContext, permissionsProvider)
