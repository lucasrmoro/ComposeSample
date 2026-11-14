# Navigation

Defines feature entry contracts and the registry that connects them without exposing presentation implementations.

## Modules

- `api/core`: feature destinations, Compose entry points, navigation registry, and cross-feature navigator contract.
- `api/<feature>`: typed destinations grouped by feature, such as `stream/StreamDestination`.
- `impl`: route registry retained for the lifetime of the app.
- `di`: DI registrations for the shared route registry.

The app builds a Navigation Compose `NavHost` from registered entry points and keeps the back stack between features. Each registered feature provides a Compose entry point that hosts its own `NavHost` and internal back stack. Feature DI registers the entry point during app startup; one feature must mark its entry point as the start destination. Presentation does not use feature Activities or XML navigation graphs.

## Setup

Load `NavigationModule.init()` in `AppApplication` before registering feature entry points. Pass the shared registry to feature DI:

```kotlin
NavigationModule.init()
StreamModule.init(navigationRegistry = get())
```

Feature DI registers `createStreamEntryPoint()` with the registry. For the initial feature, pass `isStartDestination = true`. Duplicate route names and multiple start destinations are rejected; building the host without a start destination fails with `IllegalStateException`.

## Usage

The app creates a `NavHost` destination for each registered entry point and calls its `Content` composable. A feature can request another feature through `LocalFeatureNavigator.current.navigate(destination)`. The navigator pushes a new entry; opening the same feature again creates another entry. Navigation Compose handles Back in the active feature's internal navigation first, then returns to the calling feature when that feature is at its start destination.

The app `NavHost` saves its back stack and destination state, allowing the internal `NavHost` and saveable screen state to be restored when a child feature closes. Do not retain an Activity or `NavController` in the process-wide registry.

Declare destinations as `FeatureDestination` implementations in this API. Each feature owns its internal routes and composables. Do not create a Gradle navigation module per feature.

Use the typed argument helpers from `api/core` to declare arguments for internal feature routes, such as `longArgument(key = Routes.Arguments.ID, defaultValue = 0L)`. These helpers keep argument names alongside shared identifiers while preserving the official Navigation Compose DSL.
