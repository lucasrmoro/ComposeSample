package br.com.composesample.plugins

import br.com.composesample.Plugins
import br.com.composesample.plugins.base.BasePlugin
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidAppPlugin : BasePlugin() {

    override fun setup(project: Project) = with(project) {
        setupPlugins()
        setupConfig()
    }

    private fun Project.setupPlugins() {
        apply(plugin = Plugins.ANDROID_APPLICATION)
        apply(plugin = Plugins.KOTLIN_ANDROID)
    }

    private fun Project.setupConfig() = extensions.configure<ApplicationExtension> {
        setupProjectConfig(this)
    }

}
