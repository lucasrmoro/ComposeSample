package br.com.composesample.featureA.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.composesample.featureA.form.screen.FormScreen
import br.com.composesample.featureA.form.viewModel.FormViewModel
import br.com.composesample.featureA.list.screen.ListScreen
import br.com.composesample.featureA.list.viewModel.ListViewModel
import br.com.composesample.structural.navigation.core.AnimatedNavHost
import br.com.composesample.structural.di.dependencyViewModel

private object FeatureARoute {

    const val LIST = "list"
    const val FORM = "form"

}

@Composable
internal fun FeatureANavHost(navController: NavHostController = rememberNavController()) {
    AnimatedNavHost(
        navController = navController,
        startDestination = FeatureARoute.LIST
    ) {
        composable(FeatureARoute.LIST) {
            val viewModel: ListViewModel = dependencyViewModel()
            ListScreen(
                viewModel = viewModel,
                onOpenForm = { navController.navigate(FeatureARoute.FORM) }
            ).Start()
        }
        composable(FeatureARoute.FORM) {
            val viewModel: FormViewModel = dependencyViewModel()
            FormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            ).Start()
        }
    }
}
