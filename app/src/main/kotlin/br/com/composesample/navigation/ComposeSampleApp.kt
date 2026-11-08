package br.com.composesample.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.composesample.structural.designsystem.components.SampleClearFocusOnOutsideTap
import br.com.composesample.structural.designsystem.theme.ComposeSampleTheme
import br.com.composesample.structural.navigation.core.AnimatedNavHost
import br.com.composesample.structural.navigation.core.FeatureDestination
import br.com.composesample.structural.navigation.core.NavigationRegistry
import br.com.composesample.structural.navigation.core.Navigator

@Composable
fun ComposeSampleApp(navigationRegistry: NavigationRegistry) {
    val navController = rememberNavController()
    val navigator = remember(navController) {
        Navigator { destination: FeatureDestination ->
            navController.navigate(destination.route)
        }
    }

    ComposeSampleTheme {
        SampleClearFocusOnOutsideTap {
            Surface(modifier = Modifier.fillMaxSize()) {
                AnimatedNavHost(
                    navController = navController,
                    startDestination = navigationRegistry.startDestination
                ) {
                    for (entryPoint in navigationRegistry.entryPoints) {
                        composable(route = entryPoint.route) {
                            entryPoint.Content(navigator)
                        }
                    }
                }
            }
        }
    }
}
