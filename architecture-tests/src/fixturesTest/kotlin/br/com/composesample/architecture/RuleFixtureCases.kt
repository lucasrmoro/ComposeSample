package br.com.composesample.architecture

internal data class RuleFixtureCase(
    val rule: String,
    val validFixture: String,
    val invalidFixture: String,
    val diagnostic: String
)

internal val ruleFixtureCases = listOf(
    RuleFixtureCase(
        rule = "ApplicationRulesTest.the project must declare exactly one activity in the app module",
        validFixture = "applicationRules/exactlyOneActivity/valid",
        invalidFixture = "applicationRules/exactlyOneActivity/invalid",
        diagnostic = "exactly one Activity"
    ),
    RuleFixtureCase(
        rule = "ApplicationRulesTest.launcher activity must only host the app content",
        validFixture = "applicationRules/launcherActivityHost/valid",
        invalidFixture = "applicationRules/launcherActivityHost/invalid",
        diagnostic = "launcher Activity host boundary"
    ),
    RuleFixtureCase(
        rule = "ApplicationRulesTest.activities must be declared only in the app module",
        validFixture = "applicationRules/activitiesOnlyInApp/valid",
        invalidFixture = "applicationRules/activitiesOnlyInApp/invalid",
        diagnostic = "Activity declarations are only allowed in :app"
    ),
    RuleFixtureCase(
        rule = "ContractRulesTest.structural APIs must import only approved contract types",
        validFixture = "contractRules/structuralApiImports/valid",
        invalidFixture = "contractRules/structuralApiImports/invalid",
        diagnostic = "structural API contract purity"
    ),
    RuleFixtureCase(
        rule = "ContractRulesTest.feature domain APIs must import only contract types",
        validFixture = "contractRules/featureDomainApiImports/valid",
        invalidFixture = "contractRules/featureDomainApiImports/invalid",
        diagnostic = "feature domain API purity"
    ),
    RuleFixtureCase(
        rule = "CoroutineRulesTest.production code must not launch coroutines in global scope",
        validFixture = "coroutineRules/noGlobalScope/valid",
        invalidFixture = "coroutineRules/noGlobalScope/invalid",
        diagnostic = "coroutine lifecycle ownership"
    ),
    RuleFixtureCase(
        rule = "CoroutineRulesTest.hard coded coroutine dispatchers must stay in the dispatcher provider",
        validFixture = "coroutineRules/injectedDispatchers/valid",
        invalidFixture = "coroutineRules/injectedDispatchers/invalid",
        diagnostic = "injected coroutine dispatchers"
    ),
    RuleFixtureCase(
        rule = "CoroutineRulesTest.production code must not create main scope with an implicit dispatcher",
        validFixture = "coroutineRules/explicitScopeContext/valid",
        invalidFixture = "coroutineRules/explicitScopeContext/invalid",
        diagnostic = "explicit coroutine scope context"
    ),
    RuleFixtureCase(
        rule = "CoroutineRulesTest.coroutine dispatcher factories must stay in the dispatcher provider",
        validFixture = "coroutineRules/centralizedDispatcherCreation/valid",
        invalidFixture = "coroutineRules/centralizedDispatcherCreation/invalid",
        diagnostic = "centralized coroutine dispatcher creation"
    ),
    RuleFixtureCase(
        rule = "FeatureLayerRulesTest.feature data modules must not depend on presentation or domain implementations",
        validFixture = "featureLayerRules/dataDependencyDirection/valid",
        invalidFixture = "featureLayerRules/dataDependencyDirection/invalid",
        diagnostic = "must not depend"
    ),
    RuleFixtureCase(
        rule = "FeatureLayerRulesTest.feature domain api modules must not depend on outer feature layers",
        validFixture = "featureLayerRules/domainApiDependencyDirection/valid",
        invalidFixture = "featureLayerRules/domainApiDependencyDirection/invalid",
        diagnostic = "must not depend"
    ),
    RuleFixtureCase(
        rule = "FeatureLayerRulesTest.feature domain implementations must not depend on data presentation or di",
        validFixture = "featureLayerRules/domainImplementationDependencyDirection/valid",
        invalidFixture = "featureLayerRules/domainImplementationDependencyDirection/invalid",
        diagnostic = "must not depend"
    ),
    RuleFixtureCase(
        rule = "FeatureLayerRulesTest.feature presentation modules must depend on contracts instead of implementations",
        validFixture = "featureLayerRules/presentationDependencyDirection/valid",
        invalidFixture = "featureLayerRules/presentationDependencyDirection/invalid",
        diagnostic = "must not depend"
    ),
    RuleFixtureCase(
        rule = "ImportRulesTest.production Kotlin source must not use import aliases or wildcard imports",
        validFixture = "importRules/explicitImports/valid",
        invalidFixture = "importRules/explicitImports/invalid",
        diagnostic = "explicit imports"
    ),
    RuleFixtureCase(
        rule = "ImportRulesTest.feature presentation must use design system components instead of platform widgets",
        validFixture = "importRules/presentationComponents/valid",
        invalidFixture = "importRules/presentationComponents/invalid",
        diagnostic = "presentation UI component boundary"
    ),
    RuleFixtureCase(
        rule = "ModuleRulesTest.module graph must be acyclic",
        validFixture = "moduleRules/acyclicGraph/valid",
        invalidFixture = "moduleRules/acyclicGraph/invalid",
        diagnostic = "Circular"
    ),
    RuleFixtureCase(
        rule = "ModuleRulesTest.project must use only approved top level and leaf modules",
        validFixture = "moduleRules/approvedModuleLayout/valid",
        invalidFixture = "moduleRules/approvedModuleLayout/invalid",
        diagnostic = "outside the approved module layout"
    ),
    RuleFixtureCase(
        rule = "ModuleRulesTest.feature modules must not depend on sibling features",
        validFixture = "moduleRules/featureIsolation/valid",
        invalidFixture = "moduleRules/featureIsolation/invalid",
        diagnostic = "sibling feature module"
    ),
    RuleFixtureCase(
        rule = "ModuleRulesTest.api reexports must use the approved allowlist",
        validFixture = "moduleRules/approvedApiReexports/valid",
        invalidFixture = "moduleRules/approvedApiReexports/invalid",
        diagnostic = "reexports"
    ),
    RuleFixtureCase(
        rule = "NavigationRulesTest.navigation controller references must stay inside feature navigation packages",
        validFixture = "navigationRules/navigationControllerLocation/valid",
        invalidFixture = "navigationRules/navigationControllerLocation/invalid",
        diagnostic = "navigation encapsulation"
    ),
    RuleFixtureCase(
        rule = "NavigationRulesTest.feature presentation modules must declare a complete navigation package",
        validFixture = "navigationRules/requiredNavigationPackage/valid",
        invalidFixture = "navigationRules/requiredNavigationPackage/invalid",
        diagnostic = "complete feature navigation package"
    ),
    RuleFixtureCase(
        rule = "NavigationRulesTest.feature navigation packages must contain only approved feature files",
        validFixture = "navigationRules/approvedNavigationFiles/valid",
        invalidFixture = "navigationRules/approvedNavigationFiles/invalid",
        diagnostic = "approved feature navigation files"
    ),
    RuleFixtureCase(
        rule = "NavigationRulesTest.feature navigation must use shared navigation contracts",
        validFixture = "navigationRules/sharedNavigationContracts/valid",
        invalidFixture = "navigationRules/sharedNavigationContracts/invalid",
        diagnostic = "feature navigation shared contracts"
    ),
    RuleFixtureCase(
        rule = "NavigationRulesTest.feature route declarations must use the feature prefix",
        validFixture = "navigationRules/featureRoutePrefix/valid",
        invalidFixture = "navigationRules/featureRoutePrefix/invalid",
        diagnostic = "feature route naming"
    ),
    RuleFixtureCase(
        rule = "PackageRulesTest.feature source files must stay within approved package roots",
        validFixture = "packageRules/approvedPackageRoots/valid",
        invalidFixture = "packageRules/approvedPackageRoots/invalid",
        diagnostic = "feature package roots"
    ),
    RuleFixtureCase(
        rule = "PackageRulesTest.feature UI state event and effect declarations must live in presentation model packages",
        validFixture = "packageRules/uiContractPlacement/valid",
        invalidFixture = "packageRules/uiContractPlacement/invalid",
        diagnostic = "MVI UI contract placement"
    ),
    RuleFixtureCase(
        rule = "ScreenRulesTest.screen packages must not resolve dependencies or invoke domain services directly",
        validFixture = "screenRules/screenDependencyBoundary/valid",
        invalidFixture = "screenRules/screenDependencyBoundary/invalid",
        diagnostic = "screen dependency boundary"
    ),
    RuleFixtureCase(
        rule = "ScreenRulesTest.feature screen classes must inherit from BaseScreen",
        validFixture = "screenRules/baseScreenInheritance/valid",
        invalidFixture = "screenRules/baseScreenInheritance/invalid",
        diagnostic = "feature screen BaseScreen inheritance"
    ),
    RuleFixtureCase(
        rule = "StructuralLayerRulesTest.structural modules must not depend on feature modules",
        validFixture = "structuralLayerRules/noFeatureDependencies/valid",
        invalidFixture = "structuralLayerRules/noFeatureDependencies/invalid",
        diagnostic = "depends on feature module"
    ),
    RuleFixtureCase(
        rule = "StructuralLayerRulesTest.structural api and implementation modules must not depend on di modules",
        validFixture = "structuralLayerRules/noDiDependencies/valid",
        invalidFixture = "structuralLayerRules/noDiDependencies/invalid",
        diagnostic = "depends on DI module"
    ),
    RuleFixtureCase(
        rule = "StructuralLayerRulesTest.structural implementations may be consumed only by their service di module or app",
        validFixture = "structuralLayerRules/implementationConsumers/valid",
        invalidFixture = "structuralLayerRules/implementationConsumers/invalid",
        diagnostic = "directly on implementation module"
    ),
    RuleFixtureCase(
        rule = "ViewModelRulesTest.viewmodels must expose private _uiState as public uiState through asStateFlow",
        validFixture = "viewModelRules/readOnlyStateContract/valid",
        invalidFixture = "viewModelRules/readOnlyStateContract/invalid",
        diagnostic = "private and read-only ViewModel state naming"
    ),
    RuleFixtureCase(
        rule = "ViewModelRulesTest.viewmodels must not expose mutable state holders",
        validFixture = "viewModelRules/noMutableStateExposure/valid",
        invalidFixture = "viewModelRules/noMutableStateExposure/invalid",
        diagnostic = "read-only ViewModel state"
    ),
    RuleFixtureCase(
        rule = "ViewModelRulesTest.viewmodels must not control navigation or depend on sibling feature UI",
        validFixture = "viewModelRules/noNavigationOrSiblingUi/valid",
        invalidFixture = "viewModelRules/noNavigationOrSiblingUi/invalid",
        diagnostic = "ViewModel navigation and feature boundary"
    )
)
