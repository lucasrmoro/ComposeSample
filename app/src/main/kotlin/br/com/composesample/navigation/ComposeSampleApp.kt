package br.com.composesample.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.composesample.structural.designsystem.components.SampleConnectionBanner
import br.com.composesample.structural.designsystem.components.SampleClearFocusOnOutsideTap
import br.com.composesample.structural.designsystem.components.SampleSurface
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.navigation.core.AnimatedNavHost
import br.com.composesample.structural.navigation.core.FeatureDestination
import br.com.composesample.structural.navigation.core.NavigationRegistry
import br.com.composesample.structural.navigation.core.Navigator
import br.com.composesample.structural.network.connectivity.ConnectivityStatus
import br.com.composesample.structural.network.connectivity.NetworkConnectivityProvider

@Composable
fun ComposeSampleApp(
    navigationRegistry: NavigationRegistry,
    networkConnectivityProvider: NetworkConnectivityProvider
) {
    val navController = rememberNavController()
    val navigator = remember(navController) {
        Navigator { destination: FeatureDestination ->
            navController.navigate(destination.route)
        }
    }
    val connectivity by networkConnectivityProvider.connectivity.collectAsStateWithLifecycle()

    ComposeSampleTheme(
        content = {
            SampleClearFocusOnOutsideTap(
                content = {
                    SampleSurface(
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            Column(
                                modifier = Modifier.fillMaxSize().statusBarsPadding().imePadding(),
                                content = {
                                    if (connectivity == ConnectivityStatus.OFFLINE) {
                                        SampleConnectionBanner()
                                    }
                                    AnimatedNavHost(
                                        navController = navController,
                                        startDestination = navigationRegistry.startDestination,
                                        modifier = Modifier.weight(1f),
                                        builder = {
                                            for (entryPoint in navigationRegistry.entryPoints) {
                                                composable(route = entryPoint.route) {
                                                    entryPoint.Content(navigator = navigator)
                                                }
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}
