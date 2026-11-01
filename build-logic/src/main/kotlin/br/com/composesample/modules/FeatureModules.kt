package br.com.composesample.modules

object FeatureModules {
    private const val ROOT = ":features"

    object FeatureA {
        private const val FEATURE_A = "$ROOT:featureA"

        const val ANALYTICS = "$FEATURE_A:analytics"
        const val DATA = "$FEATURE_A:data"
        const val DI = "$FEATURE_A:di"
        const val DOMAIN_API = "$FEATURE_A:domain:api"
        const val DOMAIN_IMPL = "$FEATURE_A:domain:impl"
        const val PRESENTATION = "$FEATURE_A:presentation"
    }

    object Login {
        private const val LOGIN = "$ROOT:login"

        const val ANALYTICS = "$LOGIN:analytics"
        const val DATA = "$LOGIN:data"
        const val DI = "$LOGIN:di"
        const val DOMAIN_API = "$LOGIN:domain:api"
    }
}
