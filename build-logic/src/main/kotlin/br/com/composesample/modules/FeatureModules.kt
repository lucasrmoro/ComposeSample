package br.com.composesample.modules

object FeatureModules {

    private const val ROOT = ":features"

    object Stream {
        private const val STREAM = "$ROOT:stream"
        const val ANALYTICS = "$STREAM:analytics"
        const val DATA = "$STREAM:data"
        const val DI = "$STREAM:di"
        const val DOMAIN_API = "$STREAM:domain:api"
        const val DOMAIN_IMPL = "$STREAM:domain:impl"
        const val PRESENTATION = "$STREAM:presentation"
    }

    object Login {
        private const val LOGIN = "$ROOT:login"

        const val ANALYTICS = "$LOGIN:analytics"
        const val DATA = "$LOGIN:data"
        const val DI = "$LOGIN:di"
        const val DOMAIN_API = "$LOGIN:domain:api"
    }

}
