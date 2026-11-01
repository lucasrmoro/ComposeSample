# Media storage

`media/api` exposes `MediaFile`, `WritableMediaFile`, `MediaLocation`, and `MediaStorageProvider` for app cache and shared gallery storage. References provide a URI for display or upload, stream access, size, and deletion without exposing Android storage implementation types.

The implementation uses private cache files, MediaStore pending entries on Android 10+, and public Pictures or Movies directories on older Android versions. Legacy gallery writes require `WRITE_EXTERNAL_STORAGE`. Files are created with a developer-provided base name and format extension.

Load `MediaModule` from `AppApplication` after `PermissionsModule`. Call `createOutput` for a writable item, write its stream, then call `publish`; call `discard` after a failed operation. `replace` commits a complete sibling output and returns its resulting media reference. Callers should retain the returned reference because gallery replacements can have a different URI.
