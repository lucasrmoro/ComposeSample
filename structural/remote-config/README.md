# Remote Config

Provides remote configuration through Firebase Remote Config. Consumers depend on
`remote-config/api`; Firebase and Gson remain in `impl`. The DI module registers a
singleton `RemoteConfigProvider`. `AppApplication` loads `RemoteConfigModule.init()`
and explicitly launches `initializeRemoteConfig()` after setting up DI. The DI
module only registers bindings; factories do not start initialization.

## Reading values

Use explicit getters with string keys and optional defaults:

```kotlin
val title = remoteConfig.getString("login_title", defaultValue = "Welcome")
val enabled = remoteConfig.getBoolean("login_biometric_enabled", defaultValue = false)
val retries = remoteConfig.getInt("login_max_retries", defaultValue = 3)
val timeout = remoteConfig.getLong("login_timeout_seconds", defaultValue = 30L)
val scale = remoteConfig.getFloat("banner_scale", defaultValue = 1f)
val ratio = remoteConfig.getDouble("rollout_ratio", defaultValue = 0.5)
```

These examples are illustrative; they do not define feature flags in the project.
Encapsulate business flags behind a feature-specific contract when needed.

There is no generic `get`, key wrapper, or caller-provided parser. Reads do not make
network requests. Missing remote values, unavailable Firebase, and invalid type
conversions use the supplied defaults. Defaults are an empty String, false, or zero
when omitted. Blank keys throw `IllegalArgumentException`. Empty remote strings
are valid, and floating-point values must be finite. Integer overflow uses the default.

## Parcelable models stored as JSON

```kotlin
val settings = remoteConfig.getParcelable("login_settings", LoginSettings::class.java)
```

`LoginSettings` must implement `Parcelable`. Pass its class directly to the
interface method; there is no extension or additional overload. Gson decodes the
remote JSON in `impl`; consumers supply no parser or serializer. Missing values,
JSON null, and malformed or incompatible JSON return the supplied `defaultValue`,
or null when none is provided. Object decoding follows Gson rules; business-level
validation remains with the consumer.

Parcelable is an explicit platform-bound convenience requested for object reads;
Firebase and JSON-library types remain hidden. This uses JSON, not `Parcel` binary
serialization. Top-level generic collections are outside this class-based API;
wrap collections in a concrete Parcelable model when needed.

## Startup updates

The consumer contract exposes only reads, without `isAvailable`, `fetch`, or
`activate`. `AppApplication.onCreate()` explicitly starts the update once per application process:

1. Activate values fetched and persisted during the previous run.
2. Fetch updates using the SDK's default cache interval, retaining them for the
   next startup instead of activating newly downloaded values during interaction.

Initialization runs asynchronously in a CoroutineScope created by `AppApplication`,
using the IO dispatcher registered in DI. Reads use defaults or previously active values until
activation completes; startup does not block the main thread or screen creation.
On the first configured run, newly fetched values become active on the next startup.
No real-time listeners or feature-triggered refresh operations are exposed.

The scope belongs to application startup, not to a screen or the DI module.
Coroutine cancellation propagates; SDK work already started may continue because
these Tasks do not support cancellation.
Initialization failures are sent to `Logger`, preserving defaults and previously
active values. Without Firebase configuration, initialization is skipped and reads
continue using defaults.

Defaults are supplied directly to getters, without a second XML or DataStore
registry. The Firebase SDK persists remote configuration values.

## Firebase

Follow the [Analytics configuration steps](../analytics/README.md#configure-firebase-later).
Both services share a Firebase project, but their contracts and modules are independent.
`AppApplication` initializes the default Firebase app before loading DI or starting
Remote Config. Provider factories require this order and do not initialize Firebase.
No placeholder identifiers are required to build and start the app.

References: [Remote Config on Android](https://firebase.google.com/docs/remote-config/android/get-started)
and [activation strategies](https://firebase.google.com/docs/remote-config/loading).
