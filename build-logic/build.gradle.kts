plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation(files((libs).javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.annotationJvm)
    compileOnly(libs.android.gradle.tools)
    compileOnly(libs.kotlin.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApp") {
            id = libs.plugins.sample.android.app.get().pluginId
            implementationClass = "br.com.composesample.plugins.AndroidAppPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.sample.android.library.get().pluginId
            implementationClass = "br.com.composesample.plugins.AndroidLibraryPlugin"
        }
        register("di") {
            id = libs.plugins.sample.di.get().pluginId
            implementationClass = "br.com.composesample.plugins.DIPlugin"
        }
        register("data") {
            id = libs.plugins.sample.data.get().pluginId
            implementationClass = "br.com.composesample.plugins.DataPlugin"
        }
        register("domain") {
            id = libs.plugins.sample.domain.get().pluginId
            implementationClass = "br.com.composesample.plugins.DomainPlugin"
        }
        register("presentation") {
            id = libs.plugins.sample.presentation.get().pluginId
            implementationClass = "br.com.composesample.plugins.PresentationPlugin"
        }
    }
}
