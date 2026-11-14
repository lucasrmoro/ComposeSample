package br.com.composesample.plugins

import br.com.composesample.Plugins
import br.com.composesample.plugins.base.BaseLibraryPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class DomainPlugin : BaseLibraryPlugin() {

    override fun setupPlugins(project: Project) = with(project) {
        apply(plugin = Plugins.ANDROID_LIBRARY)
        apply(plugin = Plugins.KOTLIN_ANDROID)
    }

    override fun setupDependencies(project: Project) = Unit

}
