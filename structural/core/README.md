# Core

Contains shared contracts and utilities, Flow extensions, and application state
aggregation through `AppStateProvider`.

`core/di` registers qualified dispatchers and `AppStateProvider`. The app loads
`CoreModule.init()` before resolving consumers. Session and connectivity contracts
come from `session/api` and `network/api`; their implementations are registered by
the corresponding DI modules.

`core` does not depend on `core/di`. The `DispatcherIO`, `DispatcherMain`, and
`DispatcherDefault` qualifiers and `DependencyResolver` extensions stay in the main module so
other DI layers can use them without circular dependencies. Implementations receive
dispatchers through constructors instead of accessing `Dispatchers` directly;
concrete values are selected in `core/di`.

The `UiViewModel` contract exposes `uiState` as a `StateFlow` and `uiEffect` as a
`Flow` for screen lifecycle adapters. `BaseViewModel` implements this contract,
requires subclasses to override `sendEvent`, and provides buffered one-off effect
delivery through `sendEffect`.

Library clients, vendor SDKs, and their adapters belong in dedicated structural
services, never in `core`.

HTTP Flow error handling consumes the exceptions from `network/api`. Transport SDKs
and JSON decoding remain in `network/impl`.

`onSuccess` is a terminal collector that consumes upstream failures even without
`onError`. Add `onError` before it when a failure needs an explicit response.
Exceptions thrown by the success callback still propagate.

`onError`, `onSuccess` and `launch` propagate `CancellationException` without
converting it to a domain failure or running error callbacks. `launch` consumes
other upstream failures.

`onFinish` is a terminal collector that invokes its cleanup callback exactly once
when collection completes, fails or is cancelled, including flows with no values.
It no longer invokes the callback for every emission. Cleanup runs in a
`NonCancellable` context, allowing suspending cleanup such as releasing resources.
A ViewModel can clear its loading state there. The original failure or cancellation
propagates after cleanup, unless the cleanup callback itself throws. Keep cleanup
bounded because coroutine completion waits for it.
