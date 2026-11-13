package br.com.composesample.plugins

import br.com.composesample.Plugins
import br.com.composesample.ext.implementation
import br.com.composesample.modules.Modules
import br.com.composesample.plugins.base.BasePlugin
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidAppPlugin : BasePlugin() {

    override fun setup(project: Project) = with(project) {
        setupPlugins()
        setupConfig()
        setupDependencies()
    }

    private fun Project.setupPlugins() {
        apply(plugin = Plugins.ANDROID_APPLICATION)
        apply(plugin = Plugins.KOTLIN_ANDROID)
    }

    private fun Project.setupConfig() = extensions.configure<ApplicationExtension> {
        setupProjectConfig(this)
    }

    private fun Project.setupDependencies() = dependencies {
        implementation(project(Modules.Structural.Navigation.API))
        implementation(project(Modules.Structural.Navigation.DI))
        implementation(project(Modules.Structural.Analytics.DI))
        implementation(project(Modules.Structural.RemoteConfig.DI))
        implementation(project(Modules.Structural.Notifications.DI))
        implementation(project(Modules.Structural.PushNotifications.DI))
        implementation(project(Modules.Structural.Core.MAIN))
        implementation(project(Modules.Structural.Core.DI))
        implementation(project(Modules.Structural.Biometric.API))
        implementation(project(Modules.Structural.Biometric.DI))
        implementation(project(Modules.Structural.Session.API))
        implementation(project(Modules.Structural.Session.DI))
        implementation(project(Modules.Structural.Permissions.API))
        implementation(project(Modules.Structural.Permissions.DI))
        implementation(project(Modules.Structural.Network.API))
        implementation(project(Modules.Structural.Network.DI))
        implementation(project(Modules.Structural.LocalStorage.API))
        implementation(project(Modules.Structural.LocalStorage.DI))
        implementation(project(Modules.Structural.DesignSystem.MAIN))
        implementation(project(Modules.Structural.DesignSystem.DI))
        implementation(libs.material)
        implementation(project(Modules.Structural.DI.API))
        implementation(project(Modules.Structural.Logger.API))
        implementation(project(Modules.Structural.Logger.DI))
    }

}
