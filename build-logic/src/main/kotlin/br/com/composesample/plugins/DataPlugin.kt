package br.com.composesample.plugins

import br.com.composesample.Plugins
import br.com.composesample.ext.implementation
import br.com.composesample.modules.Modules
import br.com.composesample.plugins.base.BaseLibraryPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class DataPlugin : BaseLibraryPlugin() {

    override fun setupPlugins(project: Project) = with(project) {
        apply(plugin = Plugins.ANDROID_LIBRARY)
        apply(plugin = Plugins.KOTLIN_ANDROID)
    }

    override fun setupDependencies(project: Project) = with(project) {
        dependencies {
            implementation(project(Modules.Structural.Core.MAIN))
            implementation(project(Modules.Structural.Network.API))
            implementation(project(Modules.Structural.LocalStorage.API))
            implementation(libs.coroutines)
        }
    }

}
