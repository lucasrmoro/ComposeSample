package br.com.composesample.plugins

import br.com.composesample.Plugins
import br.com.composesample.ext.debugImplementation
import br.com.composesample.ext.implementation
import br.com.composesample.modules.Modules
import br.com.composesample.plugins.base.BasePlugin
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class PresentationPlugin : BasePlugin() {

    override fun setup(project: Project) = with(project) {
        setupPlugins()
        setupConfig()
        setupDependencies()
    }

    private fun Project.setupPlugins() {
        apply(plugin = Plugins.ANDROID_LIBRARY)
        apply(plugin = Plugins.KOTLIN_ANDROID)
        apply(plugin = Plugins.COMPOSE_COMPILER)
    }

    private fun Project.setupConfig() = extensions.configure<LibraryExtension> {
        setupProjectConfig(this)
    }

    private fun Project.setupDependencies() = dependencies {
        implementation(project(Modules.Structural.Core.MAIN))
        implementation(project(Modules.Structural.DesignSystem.MAIN))
        implementation(project(Modules.Structural.Logger.API))
        implementation(project(Modules.Structural.DI.API))
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.compose.runtime)
        implementation(libs.androidx.compose.foundation)
        implementation(libs.androidx.compose.ui.core)
        implementation(libs.androidx.compose.ui.tooling.preview)
        debugImplementation(libs.androidx.compose.ui.tooling.runtime)
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.bundles.android)
    }

}
