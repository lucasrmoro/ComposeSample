package br.com.composesample.featureA.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import br.com.composesample.structural.navigation.core.FeatureEntryPoint
import br.com.composesample.structural.navigation.core.LocalFeatureNavigator
import br.com.composesample.structural.navigation.core.Navigator
import br.com.composesample.structural.navigation.featureA.FeatureADestination

fun createFeatureAEntryPoint(): FeatureEntryPoint = FeatureAEntryPointImpl()

internal class FeatureAEntryPointImpl : FeatureEntryPoint {

    override val route = FeatureADestination.route

    @Composable
    override fun Content(navigator: Navigator) {
        CompositionLocalProvider(LocalFeatureNavigator provides navigator) {
            FeatureANavHost()
        }
    }

}
