package br.com.composesample.structural.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity

internal class AndroidPermissionMapper(
    private val context: Context
) {

    @SuppressLint("InlinedApi")
    fun runtimeName(permission: Permission): String? = when (permission) {
        Permission.CAMERA -> Manifest.permission.CAMERA
        Permission.NOTIFICATIONS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }
        Permission.WRITE_EXTERNAL_STORAGE -> if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        } else {
            null
        }
    }

    fun isGranted(permission: Permission): Boolean {
        val permissionName = runtimeName(permission) ?: return true
        return context.checkSelfPermission(permissionName) == PackageManager.PERMISSION_GRANTED
    }

    fun toResult(
        activity: ComponentActivity,
        permission: Permission,
        results: Map<String, Boolean>
    ): PermissionResult {
        val name = runtimeName(permission) ?: return PermissionResult.NOT_REQUIRED
        return when (results[name]) {
            true -> PermissionResult.GRANTED
            false -> if (activity.shouldShowRequestPermissionRationale(name)) {
                PermissionResult.DENIED
            } else {
                PermissionResult.NOT_ASK_AGAIN
            }
            null -> PermissionResult.CANCELLED
        }
    }

}
