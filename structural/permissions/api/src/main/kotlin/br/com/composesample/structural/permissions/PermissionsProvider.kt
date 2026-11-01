package br.com.composesample.structural.permissions

import kotlinx.coroutines.flow.Flow

interface PermissionsProvider {

    /** Checks whether a permission is granted without showing a permission prompt. */
    fun isGranted(permission: Permission): Boolean

    /** Collect on the main thread to request [permissions]; the flow emits one result map and then completes. */
    fun requestPermissions(vararg permissions: Permission): Flow<Map<Permission, PermissionResult>>

    /** Collect on the main thread to request camera permission; the flow emits one result and then completes. */
    fun requestCameraPermission(): Flow<PermissionResult>

    /** Collect on the main thread to request notification permission; the flow emits one result and then completes. */
    fun requestNotificationsPermission(): Flow<PermissionResult>

}
