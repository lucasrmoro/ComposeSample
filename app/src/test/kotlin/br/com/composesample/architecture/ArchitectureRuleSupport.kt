package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Module

internal val featurePresentationModulePathPattern = Regex("^:features:[^:]+:presentation$")
internal val featureDomainApiModulePathPattern = Regex("^:features:[^:]+:domain:api$")
internal val structuralApiModulePathPattern = Regex("^:structural:[^:]+:api$")
internal val structuralDiModulePathPattern = Regex("^:structural:[^:]+:di$")
internal val structuralImplModulePathPattern = Regex("^:structural:[^:]+:impl$")

internal val architectureBasePackage: String =
    requireNotNull(System.getProperty("architectureBasePackage")) {
        "The app namespace must be passed to architecture tests"
    }

internal fun FileDeclarationContext.isFeaturePresentationSource(): Boolean =
    modulePath.matches(featurePresentationModulePathPattern)

internal fun FileDeclarationContext.featureName(): String =
    modulePath.featureNameFromModulePath()

internal fun String.featureNameFromModulePath(): String =
    removePrefix(":features:").substringBefore(':')

internal fun String.hasPackageSegment(segment: String): Boolean =
    split('.').contains(segment)

internal fun Module.projectDependencyPaths(includeTestDependencies: Boolean = true): Set<String> =
    dependencies
        .asSequence()
        .filterNot { dependency ->
            !includeTestDependencies &&
                (dependency.configuration.startsWith("test") || dependency.configuration.startsWith("androidTest"))
        }
        .filter { dependency -> dependency.targetPath.startsWith(":") }
        .map { dependency -> dependency.targetPath }
        .filterNot { targetPath -> targetPath == path }
        .toSet()

internal val structuralApiAllowedPlatformImports = setOf(
    "android.net.Uri",
    "android.os.Parcelable",
    "androidx.activity.ComponentActivity",
    "androidx.annotation.RequiresPermission",
    "androidx.compose.animation.EnterTransition",
    "androidx.compose.animation.ExitTransition",
    "androidx.compose.animation.core.tween",
    "androidx.compose.animation.slideInHorizontally",
    "androidx.compose.animation.slideOutHorizontally",
    "androidx.compose.runtime.Composable",
    "androidx.compose.runtime.remember",
    "androidx.compose.runtime.staticCompositionLocalOf",
    "androidx.compose.ui.Alignment",
    "androidx.compose.ui.Modifier",
    "androidx.lifecycle.ViewModel",
    "androidx.lifecycle.ViewModelStoreOwner",
    "androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner",
    "androidx.navigation.NamedNavArgument",
    "androidx.navigation.NavBackStackEntry",
    "androidx.navigation.NavGraphBuilder",
    "androidx.navigation.NavHostController",
    "androidx.navigation.NavType",
    "androidx.navigation.compose.NavHost",
    "androidx.navigation.navArgument"
)
