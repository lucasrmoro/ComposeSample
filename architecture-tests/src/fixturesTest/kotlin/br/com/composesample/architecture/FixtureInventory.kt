package br.com.composesample.architecture

import org.junit.Test
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile

internal data class FixtureRequest(val path: String, val rule: String, val valid: Boolean)

internal class FixtureInventory(val rules: Set<String>, cases: List<RuleFixtureCase>) {

    val mappings = cases.groupBy { it.rule }
    val validMappings = mappings.filter { (rule, entries) -> rule in rules && entries.size == 1 }
        .mapValues { (_, entries) -> entries.single() }
    val problems = buildList {
        (rules - mappings.keys).sorted().forEach { add("Missing valid/invalid mapping: $it") }
        (mappings.keys - rules).sorted().forEach { add("Unknown or renamed rule mapping: $it") }
        mappings.filterValues { it.size != 1 }.keys.forEach { add("Duplicate mapping: $it") }
        (cases.map { it.validFixture } + cases.map { it.invalidFixture })
            .groupingBy { it }.eachCount().filterValues { it != 1 }.keys
            .forEach { add("Fixture must belong to exactly one rule method: $it") }
        cases.filter { it.diagnostic.isBlank() || it.validFixture == it.invalidFixture }
            .forEach { add("Invalid fixture mapping: ${it.rule}") }
    }
    val requests = cases.flatMap { case ->
        listOf(
            FixtureRequest(path = case.validFixture, rule = case.rule, valid = true),
            FixtureRequest(path = case.invalidFixture, rule = case.rule, valid = false)
        )
    }

}

internal fun discoverRuleMethods(rulesJar: File): Set<String> = JarFile(rulesJar).use { jar ->
    URLClassLoader(arrayOf(rulesJar.toURI().toURL()), FixtureInventory::class.java.classLoader).use { loader ->
        jar.entries().asSequence()
            .filter { it.name.endsWith(".class") && '$' !in it.name }
            .flatMap { entry ->
                val type = loader.loadClass(entry.name.removeSuffix(".class").replace('/', '.'))
                type.methods.asSequence()
                    .filter { it.isAnnotationPresent(Test::class.java) }
                    .map { "${type.simpleName}.${it.name}" }
            }
            .toSet()
    }
}
