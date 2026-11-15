# Permissions

`permissions/api` exposes `PermissionsProvider`, `Permission`, and `PermissionResult`.
The contract accepts one or more permissions without exposing Activity or platform APIs.

The app depends on `permissions/di` and loads `PermissionsModule.init()` before opening
screens. This module registers a factory per `ComponentActivity` through the creation
function in `permissions/impl`. The implementation uses the Activity Result API and
must be obtained by the Compose host in `onCreate`, before `STARTED`. Activities are
never retained in singletons.

`PermissionsProvider.isGranted()` checks the current permission state without opening
a prompt. The application context binding supports checks in services; requests still
use the Activity-bound provider. Compose screens must collect request flows within
the screen lifecycle.

Collecting `requestPermissions(vararg permissions)` starts a request, preserves
argument order, and processes one permission at a time. New requests enter a
batch queue. Each flow emits one result map and completes. Results include
`GRANTED`, `DENIED`, `NOT_ASK_AGAIN`, `CANCELLED`, and `NOT_REQUIRED`.

Camera and notification permissions have convenience flows. Collect them on
the main thread; each collection starts a new request. Notification permission
emits `NOT_REQUIRED` before Android 13. The implementation checks the OS version
directly.

`WRITE_EXTERNAL_STORAGE` is requested only on Android 8 and 9 for legacy public
gallery writes. On Android 10 and later it returns `NOT_REQUIRED`; gallery
captures use `MediaStore` scoped storage.

PermissionNames centralizes compile-time identifiers for annotations such as RequiresPermission. Consumers use those constants rather than declaring Android permission strings themselves. Runtime checks and requests still use Permission and PermissionsProvider so Android-version conditions remain inside permissions/impl.
