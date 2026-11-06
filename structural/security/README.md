# Security

`HashingProvider` exposes deterministic SHA-256 string digests. The implementation encodes input as UTF-8 and returns lowercase hexadecimal output, so persisted keys remain stable across process restarts. Consumers depend only on api; MessageDigest and algorithm details stay in impl.

Load `SecurityModule.init()` explicitly before consumers are resolved. The stateless implementation is registered as a factory and performs no work in its constructor. This service currently hashes small strings synchronously; it does not expose file hashing or image/cache policies.

Security capabilities use dedicated contracts. Hashing is currently implemented; encryption contracts and implementations should be added here only when required. Other services consume security/api rather than MessageDigest or a vendor SDK.
