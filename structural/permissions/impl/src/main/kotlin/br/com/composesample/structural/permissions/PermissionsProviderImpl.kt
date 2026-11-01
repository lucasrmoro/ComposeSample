package br.com.composesample.structural.permissions

import androidx.annotation.MainThread
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

internal class PermissionsProviderImpl private constructor(
    context: Context,
    private val activity: ComponentActivity?
) : PermissionsProvider {

    constructor(activity: ComponentActivity) : this(activity.applicationContext, activity)

    constructor(context: Context) : this(context.applicationContext, null)

    private val permissionMapper = AndroidPermissionMapper(context)

    private val pendingRequests = mutableListOf<PermissionRequest>()

    private val launcher = activity?.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val request = pendingRequests.firstOrNull() ?: return@registerForActivityResult
        val permission = request.currentPermission ?: return@registerForActivityResult
        request.recordResult(
            permissionMapper.toResult(
                requireNotNull(activity),
                permission,
                results
            )
        )
        launchNextPermission()
    }

    override fun isGranted(permission: Permission): Boolean = permissionMapper.isGranted(permission)

    @MainThread
    override fun requestPermissions(vararg permissions: Permission): Flow<Map<Permission, PermissionResult>> = callbackFlow {
        check(Looper.myLooper() == Looper.getMainLooper()) { "Permission requests must be collected on the main thread" }
        checkNotNull(activity) { "Permission requests require a ComponentActivity" }
        pendingRequests += PermissionRequest(permissions.toList()) { results ->
            trySend(results)
            close()
        }
        if (pendingRequests.size == 1) launchNextPermission()
        awaitClose {}
    }

    override fun requestCameraPermission(): Flow<PermissionResult> =
        requestPermissions(Permission.CAMERA).map { it.getValue(Permission.CAMERA) }

    override fun requestNotificationsPermission(): Flow<PermissionResult> =
        requestPermissions(Permission.NOTIFICATIONS).map { it.getValue(Permission.NOTIFICATIONS) }

    private fun launchNextPermission() {
        while (pendingRequests.isNotEmpty()) {
            val request = pendingRequests.first()
            val permission = request.currentPermission
            when {
                permission != null -> permissionMapper.runtimeName(permission)?.let { name ->
                    checkNotNull(launcher) { "Permission requests require a ComponentActivity" }
                        .launch(arrayOf(name))
                    return
                } ?: request.recordResult(PermissionResult.NOT_REQUIRED)

                else -> completeRequest(request)
            }
        }
    }

    private fun completeRequest(request: PermissionRequest) {
        try {
            request.complete()
        } finally {
            pendingRequests.removeAt(0)
        }
    }

}
