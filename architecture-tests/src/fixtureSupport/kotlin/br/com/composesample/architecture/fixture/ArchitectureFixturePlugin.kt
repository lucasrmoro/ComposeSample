package br.com.composesample.architecture.fixture

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

class ArchitectureFixturePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.allprojects { module ->
            module.pluginManager.apply("java-library")
            module.repositories.mavenCentral()
        }
        project.subprojects { module ->
            module.extensions.getByType(SourceSetContainer::class.java).named("main").configure { sourceSet ->
                sourceSet.java.srcDir("src/main/kotlin")
            }
        }
        configureTests(project = project)
    }

    private fun configureTests(project: Project) {
        val rulesJar = project.file(project.providers.gradleProperty("architectureRulesJar").get())
        val ruleNames = project.providers.gradleProperty("architectureRuleClasses").get().split(',')
        val rulesDirectory = project.layout.buildDirectory.dir("architecture-rule-classes")
        val unpackRules = project.tasks.register("unpackArchitectureRules", Copy::class.java) { task ->
            task.from(project.zipTree(rulesJar))
            task.into(rulesDirectory)
        }
        project.dependencies.add("testImplementation", project.files(rulesJar))
        project.dependencies.add("testImplementation", "io.github.baole:konture:1.0.4")
        project.dependencies.add("testImplementation", "junit:junit:4.13.2")
        project.tasks.named("test", Test::class.java).configure { task ->
            task.reports.html.required.set(false)
            task.dependsOn("generateArchitectureLayout", unpackRules)
            task.testClassesDirs = project.files(rulesDirectory)
            task.classpath = task.classpath.plus(project.files(rulesJar, rulesDirectory))
            task.systemProperty("architectureBasePackage", "br.com.composesample")
            task.systemProperty("architectureProjectRoot", project.projectDir.absolutePath)
            task.doFirst { prepareMergedManifests(project = project) }
            task.filter { filter ->
                ruleNames.forEach { name ->
                    filter.includeTestsMatching("br.com.composesample.architecture.$name")
                }
            }
            task.testLogging { logging ->
                logging.events("failed")
                logging.exceptionFormat = TestExceptionFormat.FULL
            }
        }
    }

    private fun prepareMergedManifests(project: Project) {
        val sourceManifest = project.file("app/src/main/AndroidManifest.xml")
        if (!sourceManifest.exists()) return
        listOf("devDebug", "devRelease", "prdDebug", "prdRelease").forEach { variant ->
            val manifest = project.file(
                "app/build/intermediates/merged_manifest/$variant/" +
                    "process${variant.replaceFirstChar(Char::uppercase)}MainManifest/AndroidManifest.xml"
            )
            manifest.parentFile.mkdirs()
            sourceManifest.copyTo(target = manifest, overwrite = true)
        }
    }

}
