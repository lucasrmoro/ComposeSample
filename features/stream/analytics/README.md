# Stream analytics

Exposes `StreamAnalytics` to the feature's layers through `listLoadStarted()` and `listLoadFinished(itemCount)`.
The internal implementation depends only on `structural/analytics/api`.
The module exposes a factory for its implementation and declares no DI bindings;
the feature DI module owns registration of the contract.

Presentation can receive the contract through its constructor. Other feature layers
can depend on this module to consume the same methods without knowing event names,
building maps, or depending on Firebase. This module does not depend on those layers.

The analytics module translates feature events into `structural/analytics` events. Its
structural dependency is internal; the feature contract does not expose structural
analytics types to its consumers. Payloads contain no tokens, passwords, or personal
identifiers.
