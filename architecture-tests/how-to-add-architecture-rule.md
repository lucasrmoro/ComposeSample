# How to add an architecture rule and its validation fixtures

This guide follows a new rule from implementation through CI validation. The example
prohibits `runBlocking` in feature presentation code. It is an implementation example;
copying this document does not enable the rule in the project.

A **rule** is a JUnit method that checks a constraint. The existing **gate** is the
`architectureCheck` task, executed by the `Architecture validation` job. Adding a rule
to this gate normally requires neither another Gradle task nor another CI job.

## 1. Define the observable behavior

Before writing the test, establish:

- The target: in this example, sources in `:features:<feature>:presentation`.
- The allowed behavior: a suspending function that does not block the thread.
- The violation: importing or referencing `kotlinx.coroutines.runBlocking`.
- The expected diagnostic: `non-blocking feature presentation`.
- Any exceptions. An exception needs a justification and a valid example that
  demonstrates its purpose.

Each method gets its own fixture pair. Keep the invalid case focused on one change
that violates the constraint. A rule may contain several internal conditions; one
pair per method does not demonstrate coverage of every condition.

## 2. Implement the rule as a JUnit 4 test

Create `architecture-tests/src/test/kotlin/br/com/composesample/architecture/BlockingRulesTest.kt`:

```kotlin
package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.files
import org.junit.Test

class BlockingRulesTest {

    @Test
    fun `feature presentation must not use runBlocking`() {
        val forbiddenApi = "kotlinx.coroutines.runBlocking"
        Konture.files {
            that().satisfy { file: FileDeclarationContext -> file.isFeaturePresentationSource() }
            should().satisfy(
                description = "non-blocking feature presentation",
                predicate = { file: FileDeclarationContext ->
                    file.declaration.imports.none { import ->
                        import == forbiddenApi || import.startsWith("$forbiddenApi.")
                    } && file.declaration.usages.none { usage ->
                        usage.targetFqName == forbiddenApi || usage.targetFqName.startsWith("$forbiddenApi.")
                    }
                }
            )
        }
    }

}
```

Use the helpers in `ArchitectureRuleSupport.kt` when the constraint depends on layers
or packages already defined by the project. If the rule belongs to an existing class,
add the method there and use that class name in the following steps.

Do not hide an empty selection with `allowEmpty()` just to make a fixture pass:
the valid case must contain an element actually selected by the rule.

## 3. Create a dedicated method directory

Under `architecture-tests/src/fixturesTest/resources/fixtures`, create:

```text
blockingRules/
  noRunBlocking/
    README.md
    valid/
      .gitignore
      settings.gradle.kts
      build.gradle.kts
      features/example/presentation/
        .gitignore
        build.gradle.kts
        src/main/kotlin/br/com/composesample/example/ui/ExampleContent.kt
    invalid/
      .gitignore
      settings.gradle.kts
      build.gradle.kts
      features/example/presentation/
        .gitignore
        build.gradle.kts
        src/main/kotlin/br/com/composesample/example/ui/ExampleContent.kt
```

The `valid` and `invalid` directories are independent builds with their own files.
Do not include them in the root `settings.gradle.kts`. There is no shared source or
module baseline; each case contains only what its selected rule needs. Use example
names such as `ExampleContent`, `ExampleViewModel`, and `example`.

## 4. Declare only the required modules

Use the following structure in each case's `settings.gradle.kts`. Set the project
name to `noRunBlocking-valid` or `noRunBlocking-invalid`, matching the directory:

```kotlin
plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "noRunBlocking-valid"

include(
    ":features:example:presentation"
)
```

The version shown is used by the current fixtures. When updating Konture, keep these
fixture builds aligned with the version in the root version catalog.

The `build.gradle.kts` **at each case's root** contains:

```kotlin
plugins {
    id("sample.architecture-fixture")
}
```

The `build.gradle.kts` **in `features/example/presentation`** can contain only:

```kotlin
// Source declarations are inspected by the selected architecture rule.
```

Add the following content to both the case root's and the module's `.gitignore`:

```gitignore
/build
/.gradle
```

For a dependency rule, include both modules and declare the relationship in the
consumer's `build.gradle.kts`, for example:

```kotlin
dependencies {
    add("implementation", project(":features:example:domain:api"))
}
```

Module inclusion and relationships belong to the fixture. The support plugin does
not inject modules or example sources.

## 5. Write the valid and invalid examples

In `valid/features/example/presentation/src/main/kotlin/br/com/composesample/example/ui/ExampleContent.kt`:

```kotlin
package br.com.composesample.example.ui

suspend fun loadExample(): String = "Example"
```

In `invalid/features/example/presentation/src/main/kotlin/br/com/composesample/example/ui/ExampleContent.kt`:

```kotlin
package br.com.composesample.example.ui

import kotlinx.coroutines.runBlocking

fun loadExample(): String = runBlocking { "Example" }
```

The difference is the blocking behavior that the new rule should detect. Do not add
unrelated violations just to trigger a generic build failure.

Konture analyzes Kotlin declarations and imports. Fixture production sources are not
compiled, so this example does not need to declare the coroutines library merely to
resolve its import. Project dependencies must be declared when the relationship
between modules is the subject of the rule.

## 6. Register the method, cases, and diagnostic

Add an entry to `ruleFixtureCases` in
`architecture-tests/src/fixturesTest/kotlin/br/com/composesample/architecture/RuleFixtureCases.kt`:

```kotlin
RuleFixtureCase(
    rule = "BlockingRulesTest.feature presentation must not use runBlocking",
    validFixture = "blockingRules/noRunBlocking/valid",
    invalidFixture = "blockingRules/noRunBlocking/invalid",
    diagnostic = "non-blocking feature presentation"
)
```

`rule` is the simple class name, a period, and the exact method name without backticks.
Paths are relative to the `fixtures` root. `diagnostic` must appear in that method's
assertion; in this example, it matches the rule's `description`. When inserting the
entry into the list, keep commas between elements and no trailing comma after the
last element.

The executor discovers methods annotated with `@Test` in the compiled JAR. There is
no other class list to maintain. A new rule without a mapping, a stale mapping after
a method rename, or a case reused by another method fails the gate.

## 7. Document the case and constraint

Write the fixture README in English, following the repository's documentation
convention. Example content for `blockingRules/noRunBlocking/README.md`:

```markdown
# noRunBlocking

Rule method: `BlockingRulesTest.feature presentation must not use runBlocking`.

The valid case uses suspension. The invalid case calls runBlocking in feature presentation.
Both cases execute only this method.

Expected assertion diagnostic: `non-blocking feature presentation`.
```

Add the corresponding row to the [fixture README](src/fixturesTest/resources/fixtures/README.md)
and describe the constraint and its exceptions in
[Architecture validation](../.github/quality-gates/architecture-validation.md).
Update the informational totals for the new cases; the verified denominator comes
from method discovery, not from those numbers in the documentation.

## 8. Validate the fixtures and inspect the evidence

From the repository root, run:

```sh
./gradlew :architecture-tests:architectureFixturesTest
```

Open `architecture-tests/build/reports/architecture-fixtures/coverage.md` and check
the `BlockingRulesTest.feature presentation must not use runBlocking` row:

- The valid case must have exactly one passing JUnit result.
- The invalid case must have exactly one JUnit result failing with
  `java.lang.AssertionError` and `non-blocking feature presentation` in its diagnostic.
- The method must not be skipped, and no other method may execute in that case.

The row's links open the build logs. Original XML files are preserved in each case's
report directory. Check them when the row does not show `PASS`. The report is recreated
on every run; copy the evidence if you need to retain it.

The suite accepts `-ParchitectureFixtureWorkers=1` for sequential troubleshooting.
This command has no public method filter: the executor checks the entire inventory
to avoid presenting partial results as complete coverage.

## 9. Run the gate against the real project

Use JDK 17 and Android SDK 36, following the project setup:

```sh
./gradlew architectureCheck
```

The gate validates fixtures first, then executes the rules against the project. It
also prepares the required manifests and validates restricted external dependencies.
If the rule detects real violations, fix the consumers or define a justified and
tested exception. Do not weaken the predicate merely to obtain a successful build.

To investigate only the result against the project after the gate has prepared the
manifests, you can run:

```sh
./gradlew :architecture-tests:test --tests 'br.com.composesample.architecture.BlockingRulesTest'
```

This command does not replace fixture validation or the complete gate.

## 10. Check CI integration

The flow is already connected:

```text
pull-request-validation.yml
  → job architecture-validation
  → .github/actions/architecture-validation/action.yml
  → ./gradlew architectureCheck
  → valid/invalid fixtures
  → rules against the real project
```

Adding a JUnit rule and its mapping uses this existing flow. A fixture with no result,
a skipped test, or a configuration failure does not count as a successful invalid
case. A rule without a fixture fails with `Missing valid/invalid mapping`.

The pipeline records the gate result and publishes its general summary. The current
action does not automatically upload the detailed `coverage.md` report or fixture XML
files as artifacts; they are available locally. If publication is needed, add artifact
upload to the architecture action, including on failure, and keep the workflow focused
on orchestration.

### When a separate CI check is needed

A separate check requires more than adding a JUnit method:

1. Create the corresponding Gradle task with explicit scope and failure criteria.
2. Encapsulate execution in a local action under `.github/actions/`.
3. Add the job to `.github/workflows/pull-request-validation.yml`, preserving Android
   setup, permissions, and dependencies on metadata validation.
4. Include the job in the dependencies and results passed to `quality-report`;
   update its description in `.github/actions/quality-report/report.py`.
5. Document the new check in `.github/quality-gates/` and its index.
6. If the check must block merges, configure the repository ruleset as well. Adding
   the job to the YAML alone does not make it required by the ruleset.

Automatic fixture coverage in this guide protects the rule methods packaged in
`architecture-tests`. A separate external validation needs its own behavior checks.

## Troubleshooting common failures

| Symptom | What to check |
| --- | --- |
| `Missing valid/invalid mapping` | A discovered method has no entry in `RuleFixtureCases.kt`. |
| `Unknown or renamed rule mapping` | The class or method was renamed without updating its mapping. |
| `Fixture must belong to exactly one rule method` | Two methods point to the same case. Create a dedicated pair. |
| `Expected one JUnit result; found 0` | Check the selector, method name, build configuration, and case XML. |
| `No files/classes matched` | The fixture contains no element matching the rule's filter. |
| `Expected an architecture assertion; found PASS` | The invalid case does not violate the rule, or the predicate does not detect it. |
| `Expected diagnostic ... was absent` | The method failed for another reason, or its assertion description changed. |
| Compilation/configuration failure | Check `build.log` or `error.log`; this does not demonstrate an architecture violation. |
| `Plugin ... sample.architecture-fixture ... was not found` | Use the TestKit executor. It injects the plugin through `withPluginClasspath`; the plugin is not published to a repository. |

Before opening a pull request, confirm the method, dedicated fixture pair, fixture
README, mapping, `PASS` row in the report, and successful `architectureCheck` execution.
