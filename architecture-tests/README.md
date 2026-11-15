# Architecture tests

This JVM module owns the Konture architecture rules and their fixture support. It is
included in the root build so the explicit `architectureCheck` gate can run its rules;
the app does not depend on this module.

To add a rule and its isolated valid/invalid fixtures, follow the
[end-to-end guide](how-to-add-architecture-rule.md), from implementation and coverage
registration to local validation and CI integration.

Run the rules against the current project with:

```sh
./gradlew :architecture-tests:test
```

The module also defines a separate task for isolated valid and invalid project fixtures:

```sh
./gradlew :architecture-tests:architectureFixturesTest
```

Fixture projects are copied to temporary directories by Gradle TestKit and are not
included in the root settings, so intentionally invalid dependency graphs do not
affect app builds. Their mappings live in
`src/fixturesTest/kotlin/br/com/composesample/architecture/RuleFixtureCases.kt`.

Fixture execution support lives in `src/fixturesTest/kotlin/br/com/composesample/architecture`.
The runner uses bounded concurrency and preserves individual logs and JUnit XML
results. Fixture builds use Gradle's built-in Java library plugin and register
`src/main/kotlin` as an analysis source directory; they do not load the Kotlin Gradle
plugin or compile fixture production sources.

Gradle fixture configuration is a compiled plugin in
`src/fixtureSupport/kotlin/br/com/composesample/architecture/fixture/ArchitectureFixturePlugin.kt`.
The dedicated `fixtureSupport` source set declares `compileOnly(gradleApi())` so the
IDE can resolve Gradle API types. Only the fixture task depends on this plugin and its
compilation tasks; app assembly does not.
