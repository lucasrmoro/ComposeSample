package br.com.composesample.architecture

import java.io.File

internal fun writeCoverageReport(
    directory: File,
    rules: Set<String>,
    mappings: Map<String, List<RuleFixtureCase>>,
    checks: Map<String, RuleFixtureResult>,
    problems: List<String>
) {
    val covered = checks.values.count { it.passed }
    val rows = (rules + mappings.keys).sorted().map { rule ->
        checks[rule]?.markdownRow() ?: "| ${rule.cell()} | — | — | — | MISSING / INVALID MAPPING |"
    }
    val report = buildString {
        appendLine("# Architecture fixture coverage")
        appendLine()
        appendLine("$covered/${rules.size} discovered JUnit methods verified with both a passing valid fixture and an invalid fixture failing with the expected assertion.")
        appendLine()
        appendLine("This measures method coverage by behavioral examples, not line or branch coverage. XML results and build logs are preserved in each linked fixture directory.")
        appendLine()
        appendLine("| Rule method | Valid fixture | Invalid fixture | Expected assertion diagnostic | Result |")
        appendLine("| --- | --- | --- | --- | --- |")
        rows.forEach(::appendLine)
        if (problems.isNotEmpty()) {
            appendLine()
            appendLine("## Gate failures")
            appendLine()
            problems.forEach { appendLine("- ${it.cell()}") }
        }
    }
    directory.resolve("coverage.md").writeText(report)
    println("Architecture fixture coverage: $covered/${rules.size}. Report: ${directory.resolve("coverage.md")}")
}

internal fun printFixtureResult(result: RuleFixtureResult) {
    val status = if (result.passed) colorize("PASS", "\u001B[32m") else colorize("FAIL", "\u001B[31m")
    val scenario = colorize(result.case.validFixture.removeSuffix("/valid"), "\u001B[36m")
    val ruleClass = colorize(result.case.rule.substringBefore('.'), "\u001B[35m")
    val description = result.case.rule.substringAfter('.').replaceFirstChar(Char::uppercase)
    val details = if (result.passed) "valid/invalid" else result.status
    println("$status $scenario ($details) — $ruleClass: $description")
}

private fun RuleFixtureResult.markdownRow(): String =
    "| ${case.rule.cell()} | [${case.validFixture}](${case.validFixture}/build.log) | " +
        "[${case.invalidFixture}](${case.invalidFixture}/build.log) | ${case.diagnostic.cell()} | ${status.cell()} |"

private fun String.cell(): String = replace("|", "\\|").replace("\n", " ").replace("\r", " ")

private fun colorize(value: String, color: String): String =
    if (System.getenv("NO_COLOR") == null && System.getenv("TERM") != "dumb") "$color$value\u001B[0m" else value
