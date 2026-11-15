package br.com.composesample.architecture

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.testkit.runner.UnexpectedBuildFailure
import java.io.File
import java.nio.file.Files
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors

internal class FixtureRunner(
    private val fixturesRoot: File,
    private val workspaceRoot: File,
    private val rulesJar: File,
    private val pluginJar: File,
    private val reportDirectory: File
) {

    fun runAll(
        requests: List<FixtureRequest>,
        workerCount: Int,
        onFixtureCompleted: (FixtureRequest, FixtureResult) -> Unit
    ): Map<String, FixtureResult> {
        require(workerCount in 1..4) { "architectureFixtureWorkers must be between 1 and 4" }
        println("Running ${requests.size} isolated fixtures with $workerCount workers")
        val executor = Executors.newFixedThreadPool(workerCount)
        try {
            val completionService = ExecutorCompletionService<Pair<FixtureRequest, FixtureResult>>(executor)
            requests.forEach { request ->
                completionService.submit(Callable { request to run(request = request) })
            }
            val results = mutableMapOf<String, FixtureResult>()
            repeat(requests.size) {
                val (request, result) = completionService.take().get()
                results[request.path] = result
                onFixtureCompleted(request, result)
            }
            return results
        } finally {
            executor.shutdownNow()
        }
    }

    private fun run(request: FixtureRequest): FixtureResult {
        val evidence = reportDirectory.resolve(request.path)
        evidence.mkdirs()
        return try {
            val workspace = Files.createTempDirectory(workspaceRoot.toPath(), "fixture-").toFile()
            val project = workspace.resolve("project")
            fixturesRoot.resolve(request.path).copyRecursively(target = project)
            val build = executeBuild(project = project, rule = request.rule)
            evidence.resolve("build.log").writeText(build.output)
            val results = readFixtureResults(project = project, evidence = evidence)
            results.copy(problem = buildProblem(build = build, request = request, results = results))
        } catch (failure: Exception) {
            evidence.resolve("error.log").writeText(failure.stackTraceToString())
            FixtureResult(tests = emptyMap(), problem = "${failure.javaClass.simpleName}: ${failure.message}")
        }
    }

    private fun executeBuild(project: File, rule: String): BuildResult {
        val runner = GradleRunner.create()
            .withProjectDir(project)
            .withPluginClasspath(listOf(pluginJar))
            .withArguments(
                ":test",
                "-ParchitectureRulesJar=${rulesJar.absolutePath}",
                "-ParchitectureRuleClasses=$rule",
                "--console=plain",
                "--max-workers=1",
                "--stacktrace"
            )
        return try {
            runner.build()
        } catch (failure: UnexpectedBuildFailure) {
            failure.buildResult
        }
    }

    private fun buildProblem(build: BuildResult, request: FixtureRequest, results: FixtureResult): String? {
        val expected = if (request.valid) TaskOutcome.SUCCESS else TaskOutcome.FAILED
        val actual = build.task(":test")?.outcome
        if (actual != expected) return "Expected :test $expected; found $actual. See build.log"
        val unexpected = results.tests.keys - request.rule
        return "Unexpected JUnit methods: $unexpected".takeIf { unexpected.isNotEmpty() }
    }

}
