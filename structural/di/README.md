# Runtime dependency injection

Owns the application's DI contracts. Feature and structural DI modules depend on
`di/api`; only `di/impl` depends on Koin. Replacing the runtime backend changes the
implementation and its Gradle dependency without rewriting consumer registrations.
Compile-time DI frameworks are outside this contract.

This foundational service has `api` and `impl`, without a self-registering `di`
module. It bootstraps other DI modules and does not depend on core, features, or
design-system. Both the runtime backend and AndroidX integration belong in `impl`.
Design-system and feature consumers depend only on `api`. The app is the composition
root and selects `impl`; no consumer accesses backend classes or Android factory
implementation functions.

## Startup and shutdown

`AppApplication` explicitly starts DI and loads structural modules before features:

```kotlin
val dependencies = startDependencyInjection(this) {
    setupStructuralModules()
    setupFeatureModules()
}
```

The context overload is an Android bootstrap function in `impl`. It registers only
the application Context and Application, never an Activity. SDK initialization
remains explicit in the Application and is separate from binding registration.

Plain JVM consumers can call `startDependencyInjection { ... }` without an Android
Context. `stopDependencyInjection()` closes the backend and releases its retained
instances. Starting twice fails; a failed configuration closes the partial container.
A later start creates a new graph.

## Registrations

```kotlin
val loginModule = dependencyModule {
    singleton<LoginRepository> {
        LoginRepositoryImpl(loginRemoteDataSource = get(), analytics = get())
    }
    factory<LoginService> { LoginServiceImpl(httpClient = httpClientNoAuth) }
}

loadModules(
    loginModule
)
```

`singleton` retains one instance per type and qualifier. `factory` creates one for
each resolution. Definitions use explicit constructor lambdas; no annotations,
reflection-based constructor wiring, or DI code generation is required. Later
registrations override earlier ones, including for existing test replacements.
Definitions are lazy and must not initialize SDKs or launch background work.

## Qualifiers and parameters

Qualifier objects implement `DependencyQualifier` and expose a stable String value.
Bindings are addressed by their type and qualifier value, so different types and
qualified/unqualified registrations remain independent. Generic type arguments are
erased: use distinct contracts or qualifiers when registering multiple parameterizations
of the same generic class.

```kotlin
val permissionsModule = dependencyModule {
    factory<PermissionsProvider> { parameters ->
        createPermissionsProvider(activity = parameters.get<ComponentActivity>())
    }
}

val provider = get<PermissionsProvider> { parametersOf(activity) }
```

`DependencyParameters` copies arguments into a per-resolution list. Typed access
selects the first assignable argument, including boxed primitive values. Missing
parameters fail explicitly. Singleton parameters apply only at first construction;
Activity-bound providers must remain factories.

`DependencyResolver` is the receiver for construction lambdas and shared helpers,
such as dispatcher and Retrofit accessors. It exposes no vendor Scope. Top-level
`get` and `inject` support framework-created components; prefer constructor
injection in business code. Lazy injection resolves only at first access.

## ViewModels

Registre ViewModels com `factory { ... }` e dependências explícitas.
`di/api` expõe `DependencyViewModelProvider` e o helper Compose
`dependencyViewModel()`. O helper usa o `ViewModelStoreOwner` ativo, incluindo uma
entrada da backstack do Navigation Compose, para que cada destino mantenha seu
próprio ViewModel. `di/impl` implementa o provider com AndroidX `ViewModelProvider`,
preservando o estado salvo e a retenção durante mudanças de configuração. O
design-system consome apenas a API; não implementa DI nem a criação de ViewModels.
O helper aceita `parameters: DependencyParameters` para argumentos específicos da rota,
por exemplo `dependencyViewModel(parameters = parametersOf(mediaId))`. O provider passa
esses valores à factory depois do `SavedStateHandle`, que continua sendo criado a partir
das `CreationExtras` do owner. A definição DI pode ler cada valor com
`parameters.get(MediaId::class)` e fornecê-lo diretamente ao construtor do ViewModel.

Resolver um ViewModel diretamente com `get()` ignora a retenção do ciclo de vida.
Use o helper da API Compose para ViewModels de UI. Os contratos centrais de registro
não expõem tipos Android nem Koin. O contrato separado de ciclo de vida Android na
mesma API usa explicitamente tipos `ViewModel` e owners do AndroidX, sem expor tipos
do backend.

## Backend boundary

`DependencyContainer` resolves instances, loads `DependencyModule` declarations,
and closes the graph. `DependencyRegistry` registers factories. `di/impl` translates
these operations into Koin bindings using internal type-qualified keys. Koin types,
scopes, qualifiers, and parameter holders remain entirely inside the adapter.

The module intentionally does not introduce custom graph resolution, nested scopes,
auto-discovery, eager SDK startup, or compile-time processing. Consumers retain
constructor injection and use only the capabilities implemented by this contract.
