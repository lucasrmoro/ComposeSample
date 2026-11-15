# Architecture rule fixtures

For adding a rule and its fixtures, follow the
[end-to-end guide](../../../../how-to-add-architecture-rule.md).

Each JUnit rule method has its own directory and its own `valid` / `invalid` pair:

```text
fixtures/
  viewModelRules/
    readOnlyStateContract/
      README.md
      valid/
      invalid/
    noMutableStateExposure/
      README.md
      valid/
      invalid/
```

Each case executes exactly one mapped JUnit method. The invalid case introduces one
targeted violation; it does not reuse invalid sources from another method. Some
architecture boundaries naturally overlap (a dependency cycle also breaks layer
direction, for example). A pair isolates the selected method and avoids unrelated
invalid examples.

## Reading a case

Every `valid` and `invalid` directory contains its complete example: local Gradle
settings listing only required modules, dependency declarations when relevant, and
Kotlin sources when the rule inspects source code. The method directory's README
identifies the selected method and the invalid change. There is no shared project,
source baseline, source overlay, or automatic module inclusion.

A graph rule uses only the modules and edges needed to demonstrate its constraint.
A source rule generally uses one module and one source file. Each case is copied
directly into a private temporary project; no example sources are generated or inherited.
Some architectural constraints overlap, so the pair isolates its selected JUnit method.

The executor runs up to two independent cases concurrently by default. Set
`-ParchitectureFixtureWorkers=1` for sequential execution (values 1–4 are supported).
Every case keeps a private workspace and its own test JVM.

The executor supplies only technical Gradle/TestKit setup through the compiled
`sample.architecture-fixture` plugin in
`src/fixtureSupport/kotlin/br/com/composesample/architecture/fixture/ArchitectureFixturePlugin.kt`:
JVM plugins, repositories, the compiled rules JAR, JUnit configuration, and XML reports. Gradle's
built-in Java library plugin exposes `src/main/kotlin` to Konture; the Kotlin Gradle
plugin is unnecessary because fixture production sources are not compiled. It does not
add modules or example Kotlin sources. For Activity count cases, it copies the case's
own `app/src/main/AndroidManifest.xml` into the merged-manifest output locations
expected by the rule, without requiring an Android build.

Konture analyzes the fixture's Kotlin declarations and imports; production fixture
sources are not compiled. Referenced Android, SDK, and coroutine types do not require
runtime stubs or dependencies unless a dependency edge itself is the subject of the
rule. Gradle configuration failures never count as successful negative coverage.

## Method matrix

There are currently 34 method directories and 68 fixture cases. The mapping lives in
`src/fixturesTest/kotlin/br/com/composesample/architecture/RuleFixtureCases.kt`.

| JUnit rule method | Case directory | Invalid change |
| --- | --- | --- |
| `ApplicationRulesTest.the project must declare exactly one activity in the app module` | [applicationRules/exactlyOneActivity](applicationRules/exactlyOneActivity/README.md) | Adds one extra Activity in the app module. |
| `ApplicationRulesTest.launcher activity must only host the app content` | [applicationRules/launcherActivityHost](applicationRules/launcherActivityHost/README.md) | Adds one navigation controller import to the launcher Activity. |
| `ApplicationRulesTest.activities must be declared only in the app module` | [applicationRules/activitiesOnlyInApp](applicationRules/activitiesOnlyInApp/README.md) | Makes the feature ExampleScreen extend FragmentActivity while app still declares exactly one Activity. |
| `ContractRulesTest.structural APIs must import only approved contract types` | [contractRules/structuralApiImports](contractRules/structuralApiImports/README.md) | Adds a contract importing Job, outside the approved contract imports. |
| `ContractRulesTest.feature domain APIs must import only contract types` | [contractRules/featureDomainApiImports](contractRules/featureDomainApiImports/README.md) | Adds a contract importing Job, outside the approved contract imports. |
| `CoroutineRulesTest.production code must not launch coroutines in global scope` | [coroutineRules/noGlobalScope](coroutineRules/noGlobalScope/README.md) | Adds one direct GlobalScope reference in a production implementation. |
| `CoroutineRulesTest.hard coded coroutine dispatchers must stay in the dispatcher provider` | [coroutineRules/injectedDispatchers](coroutineRules/injectedDispatchers/README.md) | Directly imports Dispatchers.IO outside the provider. |
| `CoroutineRulesTest.production code must not create main scope with an implicit dispatcher` | [coroutineRules/explicitScopeContext](coroutineRules/explicitScopeContext/README.md) | Replaces an injected CoroutineScope context with MainScope. |
| `CoroutineRulesTest.coroutine dispatcher factories must stay in the dispatcher provider` | [coroutineRules/centralizedDispatcherCreation](coroutineRules/centralizedDispatcherCreation/README.md) | Creates a dispatcher in the consumer instead of the provider. |
| `FeatureLayerRulesTest.feature data modules must not depend on presentation or domain implementations` | [featureLayerRules/dataDependencyDirection](featureLayerRules/dataDependencyDirection/README.md) | Adds the single forbidden edge `:features:example:data → :features:example:presentation`. |
| `FeatureLayerRulesTest.feature domain api modules must not depend on outer feature layers` | [featureLayerRules/domainApiDependencyDirection](featureLayerRules/domainApiDependencyDirection/README.md) | Adds the single forbidden edge `:features:example:domain:api → :structural:design-system`. |
| `FeatureLayerRulesTest.feature domain implementations must not depend on data presentation or di` | [featureLayerRules/domainImplementationDependencyDirection](featureLayerRules/domainImplementationDependencyDirection/README.md) | Adds the single forbidden edge `:features:example:domain:impl → :features:example:data`. |
| `FeatureLayerRulesTest.feature presentation modules must depend on contracts instead of implementations` | [featureLayerRules/presentationDependencyDirection](featureLayerRules/presentationDependencyDirection/README.md) | Adds the single forbidden edge `:features:example:presentation → :features:example:domain:impl`. |
| `ImportRulesTest.production Kotlin source must not use import aliases or wildcard imports` | [importRules/explicitImports](importRules/explicitImports/README.md) | Adds one wildcard import to a data source file. |
| `ImportRulesTest.feature presentation must use design system components instead of platform widgets` | [importRules/presentationComponents](importRules/presentationComponents/README.md) | Adds one Material 3 import to a feature screen. |
| `ModuleRulesTest.module graph must be acyclic` | [moduleRules/acyclicGraph](moduleRules/acyclicGraph/README.md) | Adds one back edge from the domain API to data, closing a dependency cycle. |
| `ModuleRulesTest.project must use only approved top level and leaf modules` | [moduleRules/approvedModuleLayout](moduleRules/approvedModuleLayout/README.md) | Adds one module outside the approved top-level layout. |
| `ModuleRulesTest.feature modules must not depend on sibling features` | [moduleRules/featureIsolation](moduleRules/featureIsolation/README.md) | Adds one dependency from example presentation to the sibling otherexample feature. |
| `ModuleRulesTest.api reexports must use the approved allowlist` | [moduleRules/approvedApiReexports](moduleRules/approvedApiReexports/README.md) | Adds one API reexport missing from the allowlist. |
| `NavigationRulesTest.navigation controller references must stay inside feature navigation packages` | [navigationRules/navigationControllerLocation](navigationRules/navigationControllerLocation/README.md) | Adds one navigation controller reference outside the navigation package. |
| `NavigationRulesTest.feature presentation modules must declare a complete navigation package` | [navigationRules/requiredNavigationPackage](navigationRules/requiredNavigationPackage/README.md) | Removes the navigation package and its required files from the presentation module. |
| `NavigationRulesTest.feature navigation packages must contain only approved feature files` | [navigationRules/approvedNavigationFiles](navigationRules/approvedNavigationFiles/README.md) | Adds an extra ExampleNavigationHelper.kt next to the three approved files. |
| `NavigationRulesTest.feature navigation must use shared navigation contracts` | [navigationRules/sharedNavigationContracts](navigationRules/sharedNavigationContracts/README.md) | Removes the shared navigation contract from the feature route. |
| `NavigationRulesTest.feature route declarations must use the feature prefix` | [navigationRules/featureRoutePrefix](navigationRules/featureRoutePrefix/README.md) | Adds one route whose name lacks the Example feature prefix; shared contracts remain present. |
| `PackageRulesTest.feature source files must stay within approved package roots` | [packageRules/approvedPackageRoots](packageRules/approvedPackageRoots/README.md) | Adds one declaration under the unapproved widgets package root. |
| `PackageRulesTest.feature UI state event and effect declarations must live in presentation model packages` | [packageRules/uiContractPlacement](packageRules/uiContractPlacement/README.md) | Adds one UiState under the allowed ui root but outside model. |
| `ScreenRulesTest.screen packages must not resolve dependencies or invoke domain services directly` | [screenRules/screenDependencyBoundary](screenRules/screenDependencyBoundary/README.md) | Adds one direct use-case import to a screen that still inherits BaseScreen. |
| `ScreenRulesTest.feature screen classes must inherit from BaseScreen` | [screenRules/baseScreenInheritance](screenRules/baseScreenInheritance/README.md) | Removes BaseScreen inheritance from the screen. |
| `StructuralLayerRulesTest.structural modules must not depend on feature modules` | [structuralLayerRules/noFeatureDependencies](structuralLayerRules/noFeatureDependencies/README.md) | Adds the single forbidden edge `:structural:media:api → :features:example:domain:api`. |
| `StructuralLayerRulesTest.structural api and implementation modules must not depend on di modules` | [structuralLayerRules/noDiDependencies](structuralLayerRules/noDiDependencies/README.md) | Adds the single forbidden edge `:structural:media:api → :structural:design-system:di`. |
| `StructuralLayerRulesTest.structural implementations may be consumed only by their service di module or app` | [structuralLayerRules/implementationConsumers](structuralLayerRules/implementationConsumers/README.md) | Adds the single forbidden edge `:features:example:data → :structural:media:impl`. |
| `ViewModelRulesTest.viewmodels must expose private _uiState as public uiState through asStateFlow` | [viewModelRules/readOnlyStateContract](viewModelRules/readOnlyStateContract/README.md) | Changes only the backing state from val to var; public state remains read-only. |
| `ViewModelRulesTest.viewmodels must not expose mutable state holders` | [viewModelRules/noMutableStateExposure](viewModelRules/noMutableStateExposure/README.md) | Adds one public mutable state property while preserving the required uiState contract. |
| `ViewModelRulesTest.viewmodels must not control navigation or depend on sibling feature UI` | [viewModelRules/noNavigationOrSiblingUi](viewModelRules/noNavigationOrSiblingUi/README.md) | Adds one sibling-feature import to the ViewModel; state remains unchanged. |

## Checking coverage

Run `./gradlew architectureCheck` and open
`architecture-tests/build/reports/architecture-fixtures/coverage.md` from the repository
root. Run only fixtures with `./gradlew :architecture-tests:architectureFixturesTest`.
The report lists each discovered JUnit rule method, its valid and invalid cases,
expected assertion diagnostic, and verification result. Each case's report directory
preserves its build log and original JUnit XML evidence.

The gate discovers `@Test` methods in the compiled rules JAR. Missing, obsolete, or
duplicate mappings fail, as does sharing one case between multiple methods. Every
case must execute exactly one method. The valid case must pass, and the invalid case
must fail with `java.lang.AssertionError` and its mapped diagnostic in that method's
JUnit failure. Missing or skipped results, unexpected methods, and compilation errors
fail coverage. Results are regenerated even when coverage fails.

Coverage is measured per method and behavioral example, not per line or branch.
For example, the Activity count case rejects an extra source Activity; it does not
independently cover every merged-manifest failure condition.
