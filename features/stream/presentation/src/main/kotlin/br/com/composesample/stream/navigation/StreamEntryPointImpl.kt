package br.com.composesample.stream.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import br.com.composesample.structural.navigation.core.FeatureEntryPoint
import br.com.composesample.structural.navigation.core.LocalFeatureNavigator
import br.com.composesample.structural.navigation.core.Navigator
import br.com.composesample.structural.navigation.stream.StreamDestination

fun createStreamEntryPoint(): FeatureEntryPoint = StreamEntryPointImpl()

internal class StreamEntryPointImpl : FeatureEntryPoint {

    override val route = StreamDestination.route

    @Composable
    override fun Content(navigator: Navigator) {
        CompositionLocalProvider(LocalFeatureNavigator provides navigator) {
            StreamNavHost()
        }
    }

}
