# Local storage

Provides shared local storage: preferences and a Room database. Consumers depend
on `local-storage/api`; the app includes `local-storage/di` and initializes
`LocalStorageModule` before using contracts. The DI module depends on `api`, `impl`,
core dispatcher helpers, and feature DAOs. Consumers do not depend on implementations
or DI.

## Preferences

`PreferencesDataSource` is a DI singleton. All preferences use a single DataStore
encrypted with `datastore-tink`, including values written through ordinary methods
such as `putString`, `putBoolean`, `putInt`, and `putLong`. Plaintext storage is not
available. Methods are suspending; IO failures propagate to the caller.
`putString(key, null)` removes the entry.

The factory retains its DataStore instance in a lazy property. The data source
accesses that property on the IO dispatcher injected through DI, keeping initialization
and storage access off the main thread.

## Biometric-protected preferences

`EncryptedPreferenceType` defines native field types: `string`, `boolean`, `int`,
`long`, `float`, `double`, `stringSet`, and `byteArray`. Use `type.withValue(value)`
to create an entry matching the key's type. Custom objects are not serialized
automatically.

`putEncrypted(key, encryption, vararg entries)` takes a single-use authorization
from `BiometricProvider.enroll()` and encrypts all entries in one operation. The
complete package is saved under `key: String` in the same Tink DataStore. Writing
to that key again replaces the previous package; other keys remain unchanged.
Duplicate field keys and empty batches are rejected before encryption.

`getEncrypted(key)` returns an encrypted snapshot or `null` if no record exists.
Pass `snapshot.data` to `BiometricProvider.authenticate()` and the returned decryption
authorization to `snapshot.decrypt()`. This operation does not reread storage: it
decrypts exactly the authorized snapshot, even if the storage key was updated while
the prompt was open.

On success, use `result.data.use { values -> ... }` and retrieve each field through
`values[type]`. Access is typed and returns `null` for missing fields. Keep each
field name associated with the same type. The caller owns the result; the singleton
retains no decrypted values. Closing the result releases its references but does
not erase values already retained by the consumer.

DataStore's `PreferencesFileSerializer` converts native types into bytes. There is
no custom Protobuf schema, KSerializer, JSON, or Parcelable. The biometric package
contains a version, IV, and authenticated ciphertext, persisted as Base64.

Cryptographic authorization is consumed once. Operations return `BiometricResult`
for encryption/decryption failures and key invalidation. Misuse, such as reusing
an authorization or accessing a closed result, throws `IllegalStateException`.
The biometric flow, device states, and available errors are described in the
[biometric README](../biometric/README.md).

## Room

The shared database belongs in `impl`. `LocalStorageFactory` retains one database
instance and one DataStore factory; DI registers `PreferencesDataSource` as a
singleton and DAOs as factories. Entities, DAOs, and data sources belong in each
feature's `data` module. Those modules depend only on `local-storage/api`, avoiding
cycles with the database implementation.

During development, keep `exportSchema = false` and do not create Room migrations.
Inject dispatchers through DI; storage initialization and operations must stay off
the main thread.
