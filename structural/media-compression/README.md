# Media compression

`media-compression/api` exposes `MediaCompressionProvider` independently from camera capture. A feature invokes `compressImage` or `compressVideo` when it chooses to compress a `MediaFile`.

Options select a quality preset. Image presets use JPEG qualities 70, 65, and 60; video presets select a target bitrate ratio and also allow an explicit bitrate override. `REPLACE` returns the replacement media reference; `COPY` creates a sibling file in the same `MediaLocation`, using the requested base name or a `_compressed` suffix. Failed or non-beneficial compression leaves the source unchanged. Photo orientation is retained. Location metadata is controlled with `LocationMetadataPolicy`.

`outputNameWithoutExtension` is optional. When omitted, `REPLACE` keeps the source base name and `COPY` appends `_compressed`. When provided, the value is used as the output base name in either mode; the media extension is added by the storage service.

Load `MediaCompressionModule` from `AppApplication` after `MediaModule`. Image work runs off the main thread. Video work uses Android media codecs through Media3 and can fail when the device lacks a suitable encoder.
