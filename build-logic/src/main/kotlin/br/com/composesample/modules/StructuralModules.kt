package br.com.composesample.modules

object StructuralModules {
    private const val ROOT = ":structural"

    object Analytics {
        private const val ROOT = "${StructuralModules.ROOT}:analytics"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Biometric {
        private const val ROOT = "${StructuralModules.ROOT}:biometric"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Camera {
        private const val ROOT = "${StructuralModules.ROOT}:camera"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Media {
        private const val ROOT = "${StructuralModules.ROOT}:media"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object MediaCompression {
        private const val ROOT = "${StructuralModules.ROOT}:media-compression"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Core {
        private const val ROOT = "${StructuralModules.ROOT}:core"

        const val DI = "$ROOT:di"
        const val MAIN = ROOT
    }

    object DesignSystem {
        private const val ROOT = "${StructuralModules.ROOT}:design-system"

        const val DI = "$ROOT:di"
        const val MAIN = ROOT
    }

    object DI {
        private const val ROOT = "${StructuralModules.ROOT}:di"

        const val API = "$ROOT:api"
        const val IMPL = "$ROOT:impl"
    }

    object LocalStorage {
        private const val ROOT = "${StructuralModules.ROOT}:local-storage"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Localization {

        private const val ROOT = "${StructuralModules.ROOT}:localization"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"

    }

    object Logger {
        private const val ROOT = "${StructuralModules.ROOT}:logger"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Navigation {
        private const val ROOT = "${StructuralModules.ROOT}:navigation"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Network {
        private const val ROOT = "${StructuralModules.ROOT}:network"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Notifications {
        private const val ROOT = "${StructuralModules.ROOT}:notifications"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Permissions {
        private const val ROOT = "${StructuralModules.ROOT}:permissions"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object PushNotifications {
        private const val ROOT = "${StructuralModules.ROOT}:push-notifications"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object RemoteConfig {
        private const val ROOT = "${StructuralModules.ROOT}:remote-config"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }

    object Session {
        private const val ROOT = "${StructuralModules.ROOT}:session"

        const val API = "$ROOT:api"
        const val DI = "$ROOT:di"
        const val IMPL = "$ROOT:impl"
    }
}
