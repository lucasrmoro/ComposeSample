import br.com.composesample.ProjectConfig
import br.com.composesample.ext.stringBuildConfigField
import br.com.composesample.modules.Modules
import groovy.json.JsonOutput
import java.io.StringReader
import java.util.Properties
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

plugins {
    alias(libs.plugins.sample.android.app)
    alias(libs.plugins.compose.compiler)
}

val releaseVersion = Properties().apply {
    load(StringReader(providers.fileContents(rootProject.layout.projectDirectory.file("release.properties")).asText.get()))
}
val tmdbReadAccessToken = gradle.extra["tmdbReadAccessToken"] as String

android {
    defaultConfig {
        applicationId = ProjectConfig.APP_ID
        targetSdk = ProjectConfig.TARGET_SDK
        versionCode = releaseVersion.getProperty("versionCode").toInt()
        versionName = releaseVersion.getProperty("versionName")
        stringBuildConfigField(name = "TMDB_READ_ACCESS_TOKEN", value = "")
    }
    buildTypes.named("debug") {
        stringBuildConfigField(name = "TMDB_READ_ACCESS_TOKEN", value = tmdbReadAccessToken)
    }
}

androidComponents.onVariants(androidComponents.selector().withBuildType("release").withFlavor("default" to "dev")) {
    it.applicationId.set("${ProjectConfig.APP_ID}.dev")
}

dependencies {
    implementation(project(Modules.Structural.Analytics.IMPL))
    implementation(project(Modules.Structural.Analytics.DI))
    implementation(project(Modules.Structural.AssetsCache.DI))
    implementation(project(Modules.Structural.Biometric.API))
    implementation(project(Modules.Structural.Biometric.DI))
    implementation(project(Modules.Structural.Camera.DI))
    implementation(project(Modules.Structural.Core.MAIN))
    implementation(project(Modules.Structural.Core.DI))
    implementation(project(Modules.Structural.DI.API))
    implementation(project(Modules.Structural.DI.IMPL))
    implementation(project(Modules.Structural.DesignSystem.MAIN))
    implementation(project(Modules.Structural.DesignSystem.DI))
    implementation(project(Modules.Structural.LocalStorage.API))
    implementation(project(Modules.Structural.LocalStorage.DI))
    implementation(project(Modules.Structural.Localization.API))
    implementation(project(Modules.Structural.Localization.DI))
    implementation(project(Modules.Structural.Logger.API))
    implementation(project(Modules.Structural.Logger.DI))
    implementation(project(Modules.Structural.Media.DI))
    implementation(project(Modules.Structural.MediaCompression.DI))
    implementation(project(Modules.Structural.Navigation.API))
    implementation(project(Modules.Structural.Navigation.DI))
    implementation(project(Modules.Structural.Network.API))
    implementation(project(Modules.Structural.Network.DI))
    implementation(project(Modules.Structural.Notifications.DI))
    implementation(project(Modules.Structural.Permissions.API))
    implementation(project(Modules.Structural.Permissions.DI))
    implementation(project(Modules.Structural.PushNotifications.API))
    implementation(project(Modules.Structural.PushNotifications.DI))
    implementation(project(Modules.Structural.PushNotifications.IMPL))
    implementation(project(Modules.Structural.RemoteConfig.DI))
    implementation(project(Modules.Structural.RemoteConfig.IMPL))
    implementation(project(Modules.Structural.Security.DI))
    implementation(project(Modules.Structural.Session.API))
    implementation(project(Modules.Structural.Session.DI))
    implementation(project(Modules.Structural.VideoPlayer.API))
    implementation(project(Modules.Structural.VideoPlayer.DI))
    implementation(project(Modules.Feature.Login.DI))
    implementation(project(Modules.Feature.Stream.DI))
    constraints {
        implementation(libs.guava) {
            because("Use the Android release that fixes GHSA-xxph-c9ww-hj94")
        }
    }
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
}

tasks.register("writeReleaseInventory") {
    val flavor = providers.gradleProperty("releaseFlavor").orElse("prd")
    val output = layout.buildDirectory.file(flavor.map { "reports/release/$it-dependencies.json" })
    outputs.file(output)
    outputs.upToDateWhen { false }
    doLast {
        val variant = flavor.get()
        require(variant == "dev" || variant == "prd")
        val components = configurations.getByName("${variant}ReleaseRuntimeClasspath")
            .incoming.resolutionResult.allComponents
            .mapNotNull { it.id as? ModuleComponentIdentifier }
            .map { mapOf("group" to it.group, "name" to it.module, "version" to it.version) }
            .distinct()
            .sortedBy { "${it["group"]}:${it["name"]}:${it["version"]}" }
        output.get().asFile.apply {
            parentFile.mkdirs()
            writeText(JsonOutput.prettyPrint(JsonOutput.toJson(components)))
        }
    }
}
