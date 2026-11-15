# Architecture tests

This JVM module owns the Konture architecture rules and their Gradle TestKit fixtures.
It is included in the root build so the explicit `architectureCheck` gate can run it;
the app does not depend on this module.

To add a rule and its isolated valid/invalid fixtures, follow the
[end-to-end guide](how-to-add-architecture-rule.md), from implementation and coverage
registration to local validation and CI integration.

Run the rules against the current project with:

```sh
./gradlew :architecture-tests:test
```

Run the isolated valid and invalid project fixtures with:

```sh
./gradlew :architecture-tests:architectureFixturesTest
```

Fixture projects live under `src/fixturesTest/resources/fixtures` and are grouped by
rule class. They are copied to temporary directories by TestKit and are not included
in the root settings, so their intentionally invalid dependency graphs do not affect
app builds. The root `architectureCheck` task runs the fixtures first and then runs
the rules against the current project. See the [fixture matrix](src/fixturesTest/resources/fixtures/README.md)
for each rule method and its isolated valid/invalid cases.

After running the gate, open [the generated coverage report](build/reports/architecture-fixtures/coverage.md).
It verifies each discovered rule method against a passing valid fixture and a failing
invalid fixture using JUnit XML results, with links to preserved build logs. Missing,
obsolete, or duplicate method mappings fail the gate. Maintain the mappings in
`src/fixturesTest/kotlin/br/com/composesample/architecture/RuleFixtureCases.kt`.
This reports coverage by method and behavioral example, not line or branch coverage.

The fixture execution code has separate responsibilities:

| File | Responsibility |
| --- | --- |
| `ArchitectureFixturesTest.kt` | Orchestrate inventory, execution, reporting, and the final assertion. |
| `FixtureInventory.kt` | Discover JUnit methods, validate mappings, and create one request per case. |
| `FixtureRunner.kt` | Run isolated Gradle builds with bounded concurrency and preserve evidence. |
| `FixtureResults.kt` | Read JUnit XML and evaluate each valid/invalid pair once. |
| `FixtureCoverageReport.kt` | Render the evaluated results as Markdown. |

These files live in `src/fixturesTest/kotlin/br/com/composesample/architecture`.
The ViewModel rules use `KotlinSourceComments.kt` in the rule source set to mask
comments before checking state initializers; comment parsing stays separate from the
architecture assertions.

The fixture executor runs up to two cases concurrently by default (one on a
single-processor environment). Each case keeps its own project directory, Gradle
invocation, test JVM, logs, and XML results. Gradle worker usage inside each case is
limited to one. To change the suite concurrency, use a value from 1 to 4:

```sh
./gradlew :architecture-tests:architectureFixturesTest -ParchitectureFixtureWorkers=1
```

Fixture builds use Gradle's built-in Java library plugin and register
`src/main/kotlin` as an analysis source directory. They do not load the Kotlin Gradle
plugin or compile production fixture sources. Konture still analyzes the actual Kotlin
declarations, imports, and project dependencies. HTML test reports inside temporary
fixture builds are disabled; the coverage report and JUnit XML evidence remain enabled.

The technical fixture configuration is a compiled Gradle plugin in
`src/fixtureSupport/kotlin/br/com/composesample/architecture/fixture/ArchitectureFixturePlugin.kt`.
The dedicated `fixtureSupport` source set declares `compileOnly(gradleApi())`, giving
the IDE a regular Kotlin source model with explicit Gradle API types. Gradle TestKit
loads the packaged plugin through `withPluginClasspath`; the Gradle API is not added
to the architecture rules' runtime classpath.

Only `architectureFixturesTest` depends on `architectureFixturePluginJar` and its
compilation tasks. The app assembly graph does not depend on this support. Initial
fixture execution compiles the plugin; subsequent unchanged executions reuse it.
After syncing Gradle in Android Studio, edit the plugin class instead of a resource
script without an imported Gradle model.
