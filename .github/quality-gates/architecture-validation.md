# Architecture validation

The architecture gate runs `./gradlew architectureCheck`. Konture reads the Gradle
project layout and Kotlin declarations/imports, then the app's local unit-test task
executes the rules in `app/src/test/kotlin/br/com/composesample/architecture/`. A root
Gradle task checks external dependency coordinates against exact allowlists. The gate
runs explicitly in pull request validation and is not attached to app builds.

## ModuleRules

- The Gradle project graph has no dependency cycles.
- Only the approved top-level projects and feature/structural module layouts exist.
- Features do not depend on sibling features.
- `api` re-exports are limited to the explicit allowlist in `ModuleRulesTest`.

## LayerRules

- Feature data, domain API, domain implementation, and presentation modules follow
  the allowed dependency direction; consumers use contracts rather than feature
  implementation modules.
- Structural services do not depend on features or on structural DI modules from
  their API/implementation modules.
- Structural implementation modules are consumed only by their matching DI module
  or the app composition root. Test-only dependencies are excluded from this check.
- Feature domain modules and structural API modules may declare only the exact external
  coordinates allowlisted in the root `build.gradle.kts`. A new dependency coordinate
  in these modules fails the gate, including libraries whose namespace was not known
  when the rule was written.
- The allowlist checks directly declared dependencies in production configurations;
  it ignores test configurations and does not validate transitive dependency changes.
- Structural and feature domain API Kotlin contracts import only their own contracts,
  structural contracts, standard Kotlin/JDK types, and the explicitly approved
  coroutine or platform types. Unknown library namespaces fail the gate, even when
  they arrive through a transitive dependency.

## SourceRules

- Feature Kotlin packages stay under the module-specific roots: data (`dao`,
  `database`, `dataSource`, `dto`, `entity`, `mapper`, `repository`), domain API
  (`model`, `repository`, `useCase`), domain implementation (`useCase`), presentation
  (`model`, `ui`, `viewModel`, `navigation`, `tokens`), analytics (`analytics`), and
  DI (the feature root or `di`).
- Kotlin imports do not use aliases or wildcards.
- Feature presentation avoids Android platform widgets and Material UI APIs; reusable
  visual components come from the design system.
- `UiState`, `UiEvent`, and `UiEffect` declarations live in presentation model packages.
- Feature domain API imports do not expose platform/vendor, DTO, or entity types.
- ViewModels expose `uiState` through `.asStateFlow()` and do not publicly expose
  `MutableStateFlow` or Compose `MutableState` holders.

## ComposeRules

- ViewModels do not hold navigation controllers or import sibling feature code.
- Screen packages do not resolve DI or access repositories, use cases, data sources, or
  data implementations directly.
- Navigation controller references stay in feature navigation packages.
- Feature route declarations use the feature name as their prefix.
- Exactly one Activity class is declared in `:app` and exactly one Activity is declared
  across source manifests; launcher Activity source does not import navigation or
  feature presentation code.

## CoroutineRules

- Production Kotlin does not use `GlobalScope`.
- Direct `Dispatchers` references are limited to the core dispatcher provider module.

## Scope and limitations

These are architecture boundary rules, not general Kotlin conventions. The gate does
not check formatting, complexity, DTO JSON annotations, mapper/data-source patterns,
dispatcher injection quality beyond direct `Dispatchers` references, cancellation
handling, Compose lifecycle collection, or individual Compose component usage. Those
implementation and style checks belong in ktlint, Detekt, Android Lint, or focused
convention checks. Konture inspects Kotlin declarations; Java source declarations are
not parsed by the current rules.

## Run locally

Use JDK 17 and Android SDK 36, then run:

```sh
./gradlew architectureCheck
```
