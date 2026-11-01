package br.com.composesample.structural.permissions

internal class PermissionRequest(
    private val permissions: List<Permission>,
    private val onResult: (Map<Permission, PermissionResult>) -> Unit
) {

    private var index = 0

    private val results = linkedMapOf<Permission, PermissionResult>()

    val currentPermission: Permission?
        get() = permissions.getOrNull(index)

    fun recordResult(result: PermissionResult) {
        val permission = checkNotNull(currentPermission)
        results[permission] = result
        index++
    }

    fun complete() {
        onResult(results.toMap())
    }

}
