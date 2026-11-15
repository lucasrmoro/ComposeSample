package br.com.composesample.architecture

import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ArchitectureFixturesTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `every architecture rule passes valid fixtures and rejects invalid fixtures`() {
        val rulesJar = systemFile(name = "architectureRulesJar")
        val reportDirectory = systemFile(name = "architectureCoverageDirectory")
        reportDirectory.deleteRecursively()
        reportDirectory.mkdirs()
        val inventory = FixtureInventory(rules = discoverRuleMethods(rulesJar), cases = ruleFixtureCases)
        val problems = inventory.problems.toMutableList()
        val results = mutableMapOf<String, FixtureResult>()
        val completedResults = mutableMapOf<String, FixtureResult>()
        val fixtureCases = inventory.validMappings.values.flatMap { case ->
            listOf(case.validFixture to case, case.invalidFixture to case)
        }.toMap()
        val runner = FixtureRunner(
            fixturesRoot = File(requireNotNull(javaClass.getResource("/fixtures")).toURI()),
            workspaceRoot = temporaryFolder.root,
            rulesJar = rulesJar,
            pluginJar = systemFile(name = "architectureFixturePluginJar"),
            reportDirectory = reportDirectory
        )
        try {
            if (problems.isEmpty()) {
                val workerCount = requireNotNull(System.getProperty("architectureFixtureWorkers")).toInt()
                results.putAll(
                    runner.runAll(
                        requests = inventory.requests,
                        workerCount = workerCount,
                        onFixtureCompleted = { request, result ->
                            completedResults[request.path] = result
                            val case = fixtureCases[request.path]
                            val caseIsComplete = case?.let {
                                it.validFixture in completedResults && it.invalidFixture in completedResults
                            } == true
                            if (case != null && caseIsComplete) {
                                printFixtureResult(checkRuleFixture(case = case, results = completedResults))
                            }
                        }
                    )
                )
            }
        } finally {
            val checks = inventory.validMappings.mapValues { (_, case) -> checkRuleFixture(case = case, results = results) }
            if (problems.isEmpty()) problems.addAll(checks.values.flatMap { it.problems })
            writeCoverageReport(
                directory = reportDirectory,
                rules = inventory.rules,
                mappings = inventory.mappings,
                checks = checks,
                problems = problems
            )
        }
        assertTrue(
            "Architecture fixture coverage failed. See ${reportDirectory.resolve("coverage.md")}\n" +
                problems.joinToString(separator = "\n"),
            problems.isEmpty()
        )
    }

    private fun systemFile(name: String): File = File(requireNotNull(System.getProperty(name)))

}
