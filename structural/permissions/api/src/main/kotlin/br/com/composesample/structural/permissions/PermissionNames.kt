package br.com.composesample.structural.permissions

/**
 * Compile-time permission identifiers for static API annotations.
 * Runtime checks and requests must use [Permission] through [PermissionsProvider], which applies
 * the platform-version rules.
 */
object PermissionNames {

    const val CAMERA = "android.permission.CAMERA"

    const val NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"

    const val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"

}
