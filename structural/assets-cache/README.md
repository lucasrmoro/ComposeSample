# Assets cache

Register `AssetsCacheConfiguration` in the application composition root, then load
`AssetsCacheModule` after media, network, security and local-storage. The application
selects the private directory, byte limit, refresh age.
The sample uses 200 MiB (209,715,200 bytes) and 30 days in `app/configuration/AppConfigurationModule.kt`.

The provider coordinates cached delivery, refresh and fallback. `AssetCacheWriter`
streams HTTP responses through network/api into private files managed by media/api.
`AssetsCacheStore` accesses metadata only through local-storage/api's
`FileCacheMetadataDataSource`. Hashing is provided by security/api.

Asset bytes stay in cacheDir. Metadata lives in a separate Room database owned by
local-storage/impl. The cache does not hold a Map of entries or maintain a binary
index. Lookups fetch one row; total size uses SQL SUM; removal selects one oldest entry
at a time using an indexed query. Updating access time changes only that row.

Consumers pass `AssetRequest(url, group)`. The group selects a subdirectory without
giving it feature meaning. Stream uses posters, backdrops, cast and providers.
All groups share one namespace and one global byte limit. Last access affects
eviction; only a successful download renews freshness. Refresh keeps the previous
asset until new metadata is saved, and failed/cancelled writes restore the prior
metadata and discard the new file.

The first access to a new metadata namespace removes the previous unindexed asset
cache incrementally and marks the namespace ready in Room. This rebuild affects
only that configured asset cache; the movie/series catalog and preferences stay
intact. No initialization flag or full in-memory catalog is required. Clearing
Android's cache can leave metadata rows whose files disappeared; those rows are
removed when accessed or selected for eviction.

Constructors and DI registration perform no file/database access, downloads or
background launches. Work executes through the injected I/O dispatcher. Static
image UI and lifecycle adapters belong to design-system.

The service accepts binary assets without restricting their format to images. network/api returns the response MIME type and a safe extension derived from response metadata. The writer streams into a temporary file, then media/api publishes it with the actual type and extension; unknown types use application/octet-stream and bin. No asset payload is accumulated in memory. Image decoding and image placeholders remain in design-system consumers.
