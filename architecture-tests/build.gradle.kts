import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    alias(libs.plugins.jetbrains.jvm)
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.konture)
}

tasks.test {
    systemProperty("architectureBasePackage", "br.com.composesample")
    systemProperty("architectureProjectRoot", rootProject.rootDir.absolutePath)
}

val fixtureSupport = (extensions.getByName("sourceSets") as SourceSetContainer).create("fixtureSupport")
dependencies {
    add(fixtureSupport.compileOnlyConfigurationName, gradleApi())
}

val architectureFixturePluginJar by tasks.registering(Jar::class) {
    dependsOn(tasks.named(fixtureSupport.classesTaskName))
    archiveClassifier.set("fixture-support")
    from(fixtureSupport.output)
}

val fixturesTest = (extensions.getByName("sourceSets") as SourceSetContainer).create("fixturesTest")
kotlin.sourceSets.named("fixturesTest") {
    kotlin.srcDir("src/fixturesTest/kotlin")
}
tasks.named<ProcessResources>(fixturesTest.processResourcesTaskName) {
    doFirst {
        destinationDir.deleteRecursively()
    }
}
configurations[fixturesTest.implementationConfigurationName]
    .extendsFrom(configurations.getByName("testImplementation"))
configurations[fixturesTest.runtimeOnlyConfigurationName]
    .extendsFrom(configurations.getByName("testRuntimeOnly"))
dependencies {
    add(fixturesTest.implementationConfigurationName, gradleTestKit())
}

val architectureRulesJar by tasks.registering(Jar::class) {
    dependsOn(tasks.testClasses)
    archiveClassifier.set("architecture-rules")
    from(sourceSets.test.get().output.classesDirs)
}

val architectureFixturesTest by tasks.registering(JavaExec::class) {
    dependsOn(tasks.named(fixturesTest.classesTaskName), architectureRulesJar, architectureFixturePluginJar)
    classpath = fixturesTest.runtimeClasspath
    mainClass.set("org.junit.runner.JUnitCore")
    args("br.com.composesample.architecture.ArchitectureFixturesTest")
    systemProperty("architectureBasePackage", "br.com.composesample")
    systemProperty("architectureProjectRoot", rootProject.rootDir.absolutePath)
    systemProperty("architectureRulesJar", architectureRulesJar.get().archiveFile.get().asFile.absolutePath)
    systemProperty("architectureFixturePluginJar", architectureFixturePluginJar.get().archiveFile.get().asFile.absolutePath)
    systemProperty("architectureCoverageDirectory", layout.buildDirectory.dir("reports/architecture-fixtures").get().asFile.absolutePath)
    systemProperty(
        "architectureFixtureWorkers",
        providers.gradleProperty("architectureFixtureWorkers")
            .orElse(minOf(2, Runtime.getRuntime().availableProcessors()).toString()).get()
    )
}
