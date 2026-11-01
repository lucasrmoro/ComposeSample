package br.com.composesample.structural.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.composesample.structural.permissions.Permission
import br.com.composesample.structural.permissions.PermissionsProvider

internal class NotificationProviderImpl(
    private val context: Context,
    private val permissionsProvider: PermissionsProvider
) : NotificationProvider {

    override fun show(request: NotificationRequest): NotificationResult {
        createChannel(
            NotificationChannelSpec(
                id = request.channelId,
                name = request.channelName,
                description = request.channelDescription
            )
        )

        if (!permissionsProvider.isGranted(Permission.NOTIFICATIONS)) {
            return NotificationResult.PERMISSION_REQUIRED
        }

        val notification = NotificationCompat.Builder(context, request.channelId)
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle(request.title)
            .setContentText(request.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(request.body))
            .setAutoCancel(true)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(request.id, notification)
            NotificationResult.DISPLAYED
        } catch (_: SecurityException) {
            NotificationResult.PERMISSION_REQUIRED
        }
    }

    private fun createChannel(channel: NotificationChannelSpec) {
        val androidChannel = NotificationChannel(
            channel.id,
            channel.name,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = channel.description
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(androidChannel)
    }

}
