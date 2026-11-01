# Permissions

`permissions/api` exposes `PermissionsProvider`, `Permission`, and `PermissionResult`.
The contract accepts one or more permissions without exposing Activity or platform APIs.

O app depende de `permissions/di` e carrega `PermissionsModule.init()` antes de abrir
telas. Este módulo registra uma factory por `ComponentActivity` por meio da função de
criação em `permissions/impl`. A implementação usa a Activity Result API e deve ser
obtida pelo host Compose em `onCreate`, antes de `STARTED`. Activities nunca são
retidas em singletons.

`PermissionsProvider.isGranted()` verifica o estado atual da permissão sem abrir um
prompt. O binding de contexto da aplicação serve para verificações em serviços; as
solicitações ainda usam o provider ligado à Activity. Telas Compose devem coletar os
flows de solicitação no ciclo de vida da tela.

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
