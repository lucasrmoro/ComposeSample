package br.com.composesample.ext

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

private const val IMPLEMENTATION = "implementation"
private const val DEBUG_IMPLEMENTATION = "debugImplementation"
private const val API = "api"
private const val KSP = "ksp"

fun DependencyHandlerScope.implementation(impl: String) {
    add(IMPLEMENTATION, impl)
}

fun DependencyHandlerScope.implementation(project: Project) {
    add(IMPLEMENTATION, project)
}

fun DependencyHandlerScope.implementation(dependency: ExternalModuleDependency) {
    add(IMPLEMENTATION, dependency)
}

@JvmName("implementationLib")
fun DependencyHandlerScope.implementation(dependency: Provider<MinimalExternalModuleDependency>) {
    add(IMPLEMENTATION, dependency.get())
}

@JvmName("implementationBundle")
fun DependencyHandlerScope.implementation(dependency: Provider<ExternalModuleDependencyBundle>) {
    dependency.get().forEach { add(IMPLEMENTATION, it) }
}

fun DependencyHandlerScope.implementation(dependency: ProjectDependency) {
    add(IMPLEMENTATION, dependency)
}

fun DependencyHandlerScope.debugImplementation(
    dependency: Provider<MinimalExternalModuleDependency>
) {
    add(DEBUG_IMPLEMENTATION, dependency.get())
}

fun DependencyHandlerScope.api(dependency: ProjectDependency) {
    add(API, dependency)
}

fun DependencyHandlerScope.ksp(impl: String) {
    add(KSP, impl)
}

fun DependencyHandlerScope.ksp(dependency: Provider<MinimalExternalModuleDependency>) {
    add(KSP, dependency.get())
}
