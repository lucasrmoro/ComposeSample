# Biometrics and typed storage

The biometric service prepares a Cipher and requests biometric authorization.
Local storage accepts an authorized operation, serializes native preferences,
encrypts them, and persists the result. The native Cipher remains in `biometric/impl`.
APIs expose Kotlin interfaces without platform types.

`BiometricProvider` provides:

- `getStatus()`: checks strong biometric availability on the device.
- `enroll(prompt)`: returns `BiometricEncryption`, authorized to encrypt one
  preference package. It does not receive or persist preference values.
- `authenticate(data, prompt)`: uses the stored IV and returns
  `BiometricDecryption`, authorized to decrypt that data.

## DI configuration

The app depends on `biometric/di` and loads `BiometricModule.init()`. This module
registers a shared `BiometricProviderFactory` that retains Keystore access and
creates a `BiometricProvider` per Activity. Consumers depend on `biometric/api`;
`impl` declares no DI bindings and does not depend on `di`.

A adaptação ao ciclo de vida pertence ao design-system. O app também carrega
`DesignSystemModule.init()` de `design-system/di` antes de abrir uma tela consumidora.
Isso registra o binding qualificado `ScreenBiometricProvider`. Somente telas Compose
que usam biometria declaram a dependência de `biometric/api` e obtêm um provider com
a `FragmentActivity` host e o escopo de ciclo de vida da tela. A Activity launcher
estende `FragmentActivity` apenas porque o AndroidX BiometricPrompt exige esse host;
a UI continua em Compose. O adaptador cancela operações quando a tela é destruída ou
a coroutine chamadora é cancelada.

Persistence is registered by `LocalStorageModule.init()` from `local-storage/di`.

`PreferencesDataSource` provides:

- `putEncrypted(key, encryption, vararg entries)`: serializes entries, encrypts the
  batch once, and saves the package under `key: String`.
- `getEncrypted(key)`: reads the package once and returns an `EncryptedPreferences`
  encrypted snapshot, or `null` if the key does not exist.

After `authenticate(stored.data, prompt)`, call `stored.decrypt(decryption)`. This
decrypts the snapshot without rereading DataStore and returns `DecryptedPreferences`.
`values[preference]` retrieves typed values without reusing the Cipher.

## Typed native preferences

`EncryptedPreferenceType` provides factories for native preference types: `string`,
`boolean`, `int`, `long`, `float`, `double`, `stringSet`, and `byteArray`. For example,
`EncryptedPreferenceType.string("sample_text")` defines a text field.
`preference.withValue(value)` binds a value to its preference type; incompatible
values are rejected by the compiler.

`key: String` identifies the encrypted package in DataStore. Reading requires
neither serializers nor a field list: the package already contains native types.
Missing entries return `null`. Keep each field key associated with the same type,
as with ordinary preferences. This API does not automatically serialize custom objects.

Every successful `putEncrypted()` replaces the complete package under that key.
Use another storage key and authorization to save a separate set. Adding a field
to an existing package requires reading previous values and writing the complete
set again; the module does not silently merge encrypted content.

## Storage and data lifecycle

Local storage builds `MutablePreferences` in memory and uses DataStore's
`PreferencesFileSerializer` to convert values into bytes. Biometrics encrypts those
bytes once. Reading reverses the flow with the same serializer. There is no second
DataStore or intermediate disk write. AndroidX types remain restricted to `impl`.

There are no Batch models, Kotlin serialization plugin, KSerializer, or direct
kotlinx.serialization/Protobuf dependencies. DataStore manages its existing internal
binary format. Duplicate entry keys are rejected before encryption.

`PreferencesDataSource` is a DI singleton and retains no decrypted values. The
caller owns the encrypted snapshot, which supplies the correct IV when preparing
the Cipher. The caller also owns the decrypted result; use
`result.data.use { values -> ... }` to release references after use. `close()` does
not guarantee erasure of Strings or objects already retained by consumers.

`EncryptedPreferencesImpl` is a private inner class of `PreferencesDataSourceImpl`.
It represents the snapshot returned by `getEncrypted(key)` and uses the outer
implementation's codec and IO dispatcher.

`PreferencesDataStoreFactory` exposes a lazy `dataStore` property without an
intermediate `create()` method. Reads and writes access it inside
`withContext(ioDispatcher)`, keeping even the first initialization off the main
thread. DI configures the dispatcher and singleton.

The biometric layer encrypts the package before writing it into the same existing
Tink-encrypted DataStore. The Base64 package contains a version, IV, and ciphertext
with an authentication tag. The Keystore key is reused while valid; each encryption
creates its own Cipher and IV.

Neither `Parcelable` nor `Parcel.marshall()` is used for persistence. Android's
Parcel format is intended for IPC and can change between versions, making older
persisted data unreadable.

## Authorization per operation

Each `BiometricEncryption` or `BiometricDecryption` can be consumed once. Reuse
throws `IllegalStateException`; request another authorization for another operation.
All batch values are processed in a single Cipher call, so one biometric
authentication authorizes the batch.

Decryption uses exactly the snapshot passed to `authenticate()`. Updating the
storage key while the prompt is open does not change that snapshot. Authorization
for another package cannot decrypt it.

AES/GCM with a 256-bit key protects content and integrity. The key stays in Android
Keystore and requires strong biometrics for every use. Data is not portable between
installations or devices. Decrypted bytes are never logged. DataStore IO failures
follow the preferences contract and propagate to the caller.

## Device status and activation switch

| Status | Meaning |
| --- | --- |
| `AVAILABLE` | Strong biometrics are enrolled and available. |
| `NOT_ENROLLED` | No enrolled credentials satisfy strong biometric requirements. |
| `NO_HARDWARE` | No hardware supports the requested biometrics. |
| `HARDWARE_UNAVAILABLE` | Hardware is temporarily unavailable. |
| `SECURITY_UPDATE_REQUIRED` | The sensor requires a security update. |
| `UNSUPPORTED` | Options are incompatible with the Android version. |
| `UNKNOWN` | The system could not determine availability. |

Use `AVAILABLE` to allow enabling a switch and persist activation only after saving
values successfully. The feature owns the activation preference. Allow disabling
it even when hardware becomes unavailable. Check status again in `onResume`,
especially after returning from device settings.

Status describes device capability. It does not inspect the key, activation
preference, or screen state and does not guarantee a future operation will succeed.

## Returned failures

| Error | Meaning |
| --- | --- |
| `NOT_ENROLLED` | No strong biometrics are enrolled. |
| `UNAVAILABLE` | Biometrics are unavailable for another reason; check `getStatus()`. |
| `BIOMETRIC_REACTIVATION_REQUIRED` | The key was permanently invalidated; sign in again and reactivate biometrics. |
| `KEY_NOT_FOUND` | No key exists to decrypt the stored data. |
| `LOCKED_OUT` | The system blocked authentication after too many attempts. |
| `OPERATION_IN_PROGRESS` | Another request is running or being terminated. |
| `ENCRYPTION_FAILED` | Preparing, serializing, or encrypting preferences failed. |
| `DECRYPTION_FAILED` | Preparing, decrypting, or reconstructing stored preferences failed. |
| `CANCELLED` | The user or system cancelled the prompt. |
| `AUTHENTICATION_FAILED` | The system ended authentication with another error or returned no authorized Cipher. |

An invalidated key is reported to the caller and never replaced automatically.
The error identifies invalidation, not its unique cause. Credential recovery and
the decision to reset the key belong to the application flow.

Configuration and lifecycle are preconditions. `BiometricPromptConfig` rejects
blank titles or cancellation labels with `IllegalArgumentException`. `enroll()` and
`authenticate()` require a `RESUMED` screen; calls outside that state throw
`IllegalStateException`. Cancelling the coroutine cancels the prompt and propagates
`CancellationException`. An unrecognized biometric attempt keeps the prompt open
for another attempt. Sensor and Keystore integration must be validated on a device
with strong biometrics enrolled.

References: [biometric authentication](https://developer.android.com/identity/sign-in/biometric-auth),
[Keystore](https://developer.android.com/privacy-and-security/keystore),
[PreferencesFileSerializer](https://developer.android.com/reference/kotlin/androidx/datastore/preferences/core/PreferencesFileSerializer),
and [Parcel](https://developer.android.com/reference/android/os/Parcel).
