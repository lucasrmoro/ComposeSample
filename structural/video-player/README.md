# Video playback

`VideoPlaybackProvider.prepare` returns a cold Flow containing one `VideoEmbed`.
It accepts YouTube video references, validates their keys and builds an embedded
player document. It performs no download, network request or UI rendering.

The app owns `VideoPlaybackConfiguration` in `AppConfigurationModule` and explicitly
loads `VideoPlayerModule` from `AppApplication`. DI registration does not prepare
or play a video. The configured HTTPS origin supplies both the document base URL
and the embed origin parameter; credentials, paths, queries and fragments are
rejected when collected. A trailing root slash is normalized.

`impl` owns the `embed.html` asset and provider-specific URL construction. The
document is read on the injected I/O dispatcher when the Flow is collected.
UI adapters consume the prepared document; playback requires connectivity. The
configuration uses the sample application's origin and must match the consuming
application's identity.
