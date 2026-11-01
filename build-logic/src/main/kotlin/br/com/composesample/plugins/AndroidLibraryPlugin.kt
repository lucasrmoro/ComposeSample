package br.com.composesample.plugins

import br.com.composesample.Plugins
import br.com.composesample.ext.implementation
import br.com.composesample.plugins.base.BasePlugin
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryPlugin : BasePlugin() {

    override fun setup(project: Project) = with(project) {
        setupPlugins()
        setupConfig()
        setupDependencies()
    }

    private fun Project.setupPlugins() {
        apply(plugin = Plugins.ANDROID_LIBRARY)
        apply(plugin = Plugins.KOTLIN_ANDROID)
    }

    private fun Project.setupConfig() {
        extensions.configure<LibraryExtension> {
            setupProjectConfig(this)
        }
    }

    private fun Project.setupDependencies() {
        dependencies {
            implementation(libs.annotationJvm)
            implementation(libs.coroutines)
        }
    }
}