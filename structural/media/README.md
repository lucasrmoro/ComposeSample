# Media storage

`media/api` exposes `MediaFile`, `WritableMediaFile`, `MediaLocation`, and `MediaStorageProvider` for app cache and shared gallery storage. References provide a URI for display or upload, stream access, size, and deletion without exposing Android storage implementation types.

The implementation uses private cache files, MediaStore pending entries on Android 10+, and public Pictures or Movies directories on older Android versions. Legacy gallery writes require `WRITE_EXTERNAL_STORAGE`. Files are created with a developer-provided base name and format extension.

Load `MediaModule` from `AppApplication` after `PermissionsModule`. Call `createOutput` for a writable item, write its stream, then call `publish`; call `discard` after a failed operation. `replace` commits a complete sibling output and returns its resulting media reference. Callers should retain the returned reference because gallery replacements can have a different URI.

`createOutput` declares a conditional `RequiresPermission` annotation using the
identifier from `permissions/api`. Callers must check and request legacy storage
permission before gallery writes on Android 9 or lower. Private cache writes and
new gallery writes through MediaStore on Android 10+ do not require it. The
annotation documents the contract; runtime permission checks remain in place.

## Private cache files

`scanCache` recursively emits only private cache files on the injected I/O dispatcher;
it rejects gallery locations and does not observe later changes. Every scan also launches
cleanup of abandoned managed-write temporary files on the I/O dispatcher, in parallel with
enumeration. Active writes in this process are protected from cleanup. Temporary files are
never emitted. Collection waits for cleanup, but access and deletion failures during cleanup do not fail
the scan. Undeleted temporary files are retried on later scans. Cancelling collection also
cancels cleanup. `findCache` resolves a
validated file name directly and returns null for missing files. Synchronous lookup must
run off the main thread. Cache retention and byte limits belong to the consuming service.

For managed cache writes, collect `createOutput(location, nameWithoutExtension, writer)`.
The `MediaContentWriter` writes to the supplied stream and returns a Flow emitting exactly
one `MediaFormat`, including formats discovered during a download. The provider waits for
that flow to complete, closes the stream, validates the extension and moves the temporary
file to its final destination before emitting one `MediaFile`. Existing files are not
overwritten. Every collection starts a new write on the injected I/O dispatcher.

Errors and cancellation trigger cleanup and propagate unchanged, including
`CancellationException`. Writers must cooperate with cancellation. Cleanup failures are
suppressed on the original exception. Temporary files live outside the scanned media cache,
so incomplete files left by cleanup failure or process termination are not cache entries.
There is no public `publishCache` step. The original writable-output overload remains for
external writers such as camera capture and for replacement workflows; those consumers
still own publication and discard.

## Internal organization

`MediaStorageProviderImpl` coordinates permission checks and storage routing. `MediaCacheStore`
owns cache scanning, managed writes and temporary-file recovery; `MediaGalleryStore` owns
MediaStore creation and replacement. `MediaStoragePaths` centralizes path and name validation.
`StoredMediaFile` encapsulates streams, URIs, publication and deletion. All collaborators remain
internal to `media/impl`. The factory supplies the application context and collaborators through
constructor injection, sharing the same path validator. Construction starts no background work.
