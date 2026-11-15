package br.com.composesample.architecture

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

internal data class FixtureTestResult(
    val skipped: Boolean,
    val failureType: String?,
    val failureMessage: String?,
    val failure: String
)

internal data class FixtureResult(
    val tests: Map<String, List<FixtureTestResult>>,
    val problem: String? = null
) {

    fun check(rule: String, diagnostic: String?): String? {
        problem?.let { return it }
        val results = tests[rule].orEmpty()
        if (results.size != 1) return "Expected one JUnit result; found ${results.size}"
        val result = results.single()
        if (result.skipped) return "JUnit test was skipped"
        if (diagnostic == null) {
            return result.failureType?.let { "Unexpected failure: ${result.failure.take(240)}" }
        }
        if (result.failureType != "java.lang.AssertionError") {
            return "Expected an architecture assertion; found ${result.failureType ?: "PASS"}"
        }
        if (!result.failureMessage.orEmpty().contains(diagnostic, ignoreCase = true)) {
            return "Expected diagnostic '$diagnostic' was absent from the assertion message"
        }
        return null
    }

}

internal data class RuleFixtureResult(
    val case: RuleFixtureCase,
    val validProblem: String?,
    val invalidProblem: String?
) {

    val passed: Boolean
        get() = validProblem == null && invalidProblem == null
    val problems: List<String>
        get() = listOfNotNull(
            validProblem?.let { "${case.rule}: valid fixture: $it" },
            invalidProblem?.let { "${case.rule}: invalid fixture: $it" }
        )
    val status: String
        get() = if (passed) "PASS" else listOfNotNull(
            validProblem?.let { "Valid: $it" },
            invalidProblem?.let { "Invalid: $it" }
        ).joinToString(separator = "; ")

}

internal fun checkRuleFixture(case: RuleFixtureCase, results: Map<String, FixtureResult>): RuleFixtureResult =
    RuleFixtureResult(
        case = case,
        validProblem = checkFixture(results = results, path = case.validFixture, rule = case.rule, diagnostic = null),
        invalidProblem = checkFixture(results = results, path = case.invalidFixture, rule = case.rule, diagnostic = case.diagnostic)
    )

private fun checkFixture(
    results: Map<String, FixtureResult>,
    path: String,
    rule: String,
    diagnostic: String?
): String? {
    val result = results[path] ?: return "Not executed"
    return result.check(rule = rule, diagnostic = diagnostic)
}

internal fun readFixtureResults(project: File, evidence: File): FixtureResult {
    val factory = DocumentBuilderFactory.newInstance().apply {
        setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    }
    val results = mutableMapOf<String, MutableList<FixtureTestResult>>()
    val xmlFiles = project.resolve("build/test-results/test").listFiles().orEmpty()
        .filter { file -> file.extension == "xml" && file.name.startsWith("TEST-") }
    xmlFiles.forEach { file ->
        file.copyTo(target = evidence.resolve(file.name), overwrite = true)
        val cases = factory.newDocumentBuilder().parse(file).getElementsByTagName("testcase")
        for (index in 0 until cases.length) {
            val element = cases.item(index) as org.w3c.dom.Element
            val className = element.getAttribute("classname").substringAfterLast('.')
            val name = element.getAttribute("name")
            val failure = element.getElementsByTagName("failure").item(0)
                ?: element.getElementsByTagName("error").item(0)
            val key = "$className.$name"
            results.getOrPut(key) { mutableListOf() }.add(
                FixtureTestResult(
                    skipped = element.getElementsByTagName("skipped").length > 0,
                    failureType = failure?.attributes?.getNamedItem("type")?.nodeValue,
                    failureMessage = failure?.attributes?.getNamedItem("message")?.nodeValue,
                    failure = failure?.textContent.orEmpty()
                )
            )
        }
    }
    return FixtureResult(tests = results)
}
