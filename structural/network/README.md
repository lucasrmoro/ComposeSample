# Network

Provides HTTP communication and connectivity contracts. Feature code depends only on
`network/api`; Retrofit, OkHttp, and Gson are implementation details of `network/impl`.
The API does not depend on `core`.

## Modules and DI

- `api`: `HttpClient`, request and response type models, JSON field names, exceptions,
  connectivity, and client qualifiers.
- `impl`: HTTP transport, JSON conversion, timeouts, logging, authentication, and connectivity.
- `di`: registrations using the project's DI contracts and composition of internal HTTP adapters.

The app calls `NetworkModule.init()` explicitly in `AppApplication`. The default
base URL comes from the network environment configuration through the DI registration of
`HttpClientConfiguration`. Override that binding before resolving clients when another
environment is required; `NetworkModule.init()` takes no arguments.
Load `CoreModule.init()` for dispatchers
and the login, session, and local-storage DI modules before resolving authenticated requests.
Registration does not perform requests or initialize background work. DI creates and registers
the authenticator and authorization interceptor, then passes them into `HttpClientFactory`.
These adapter contracts are used only by network composition; features resolve `HttpClient`.

Each qualifier has one shared client:

- `AuthenticatedHttpClient`: adds the stored access token and installs the token-refresh
  authenticator, which consumes `LoginRepository` from `login/domain/api`.
- `UnauthenticatedHttpClient`: has neither the authorization interceptor nor the authenticator.
  The login token-refresh service must use this client to avoid recursive authentication.

Feature DI can use `DependencyResolver.httpClient` and `httpClientNoAuth`, or resolve
`HttpClient` directly with a qualifier. It registers its own typed service implementations:

```kotlin
singleton<FeatureAService> { FeatureAServiceImpl(httpClient = httpClient) }
```

## Requests and typed responses

```kotlin
import br.com.composesample.structural.network.http.HttpClient
import br.com.composesample.structural.network.http.HttpRequest
import br.com.composesample.structural.network.http.get
import br.com.composesample.structural.network.http.post

val items = client.get<List<SomeDTO>>(
    HttpRequest(
        path = "items/{id}",
        pathParameters = mapOf("id" to id),
        query = mapOf("page" to "1"),
        headers = mapOf("Accept-Language" to "en")
    )
)

val created = client.post<SomeDTO>(HttpRequest("items"), body = payload)
val ignored = client.delete<Unit>(HttpRequest("items/{id}", mapOf("id" to id)))
```

`get`, `post`, `put`, and `delete` have reified extensions that preserve collection and
nested generic types through the library-independent `HttpResponseType<T>`. The interface
accepts this type explicitly so implementations do not need inline methods.

Paths are relative to the configured base URL. Pass raw path and query values separately;
path substitutions are encoded as individual segments. Absolute URLs, unresolved placeholders,
and dot segments in path parameter values are rejected. Query and header maps support one
value per name. Requests and responses use JSON; request bodies are DTOs or native JSON-compatible
values. A null POST, PUT, or DELETE body is encoded as JSON null. Multipart uploads, streaming,
and binary response handling are outside this contract.

Use `Unit` for endpoints without a response body, including HTTP 204. Other response types
require a non-null JSON payload. `JsonName` maps DTO fields independently of the serialization SDK:

```kotlin
import br.com.composesample.structural.network.http.JsonName

data class LoginDTO(
    @JsonName("AccessToken") val accessToken: String?,
    @JsonName("RefreshToken") val refreshToken: String?
)
```

Annotate every serialized request/response DTO field, including nested DTOs, with `JsonName`,
including names identical to the property,
to preserve mappings in minified builds. Missing or blank annotations cause
`HttpSerializationException`; the implementation never falls back to Kotlin field names.
Consumer rules retain these fields and captured
response types. Optional JVM TLS provider references from OkHttp are handled by
implementation consumer rules; they do not add providers to the Android app.
Request serialization runs on the injected I/O dispatcher. JSON responses are decoded on
the transport's background callback thread through a Reader, without allocating an intermediate
response String. Successful response bodies use streaming conversion. The call remains linked
to cancellation until decoding finishes, and both success and error bodies are closed afterwards.
Retrofit still buffers non-success bodies internally. Debug BODY logging may also buffer bodies;
release builds do not include that interceptor.

## Failures

- Non-2xx responses throw the API's `HttpException`, with `statusCode` and a decoded
  `ApiException`. Empty or unrecognized server error payloads use an empty `ApiException`.
- Invalid JSON and missing bodies for non-Unit responses throw `HttpSerializationException`.
- Transport failures propagate as standard I/O exceptions; cancellation is not converted
  into an HTTP error.

The core Flow extensions consume these API errors without depending on Retrofit or Gson.
The implementation configures call/read timeouts of 30 seconds. HTTP logging is configured
only in `src/debug`, using a `debugImplementation` dependency. `src/release` provides a
no-op builder extension; the logging interceptor dependency is absent from release APKs.

## Binary downloads

`BinaryDownloadProvider.download` returns a cold Flow that streams an HTTPS
response into a caller-owned output stream and emits `BinaryDownloadResult` with
byte count, MIME type and extension. `NetworkModule` registers the provider with
the injected I/O dispatcher. The caller supplies a byte limit; the downloader
rejects embedded URL credentials and does not follow redirects.
Its independent HTTP client does not attach app authentication headers.

Cancellation cancels the underlying request. The response body is closed by the
provider; the output stream remains owned by the caller and may contain partial
data after a failure. Callers are responsible for discarding incomplete files.

Binary downloads reuse the Retrofit streaming service and its cancellable call adapter.
NetworkModule passes shared network components into the downloader composition function.
HttpClientFactory owns the independent download client and its redirect configuration.
Response bytes are copied to the output in bounded chunks.

## JSON codec

`JsonCodec` encodes annotated objects and decodes JSON through the existing
`HttpResponseType<T>` contract, including generic types. `NetworkModule` registers
the codec; consumers depend on `network/api`, while Gson remains inside `impl`.
The codec reuses the HTTP client's strict JSON and explicit `JsonName` mapping.
Serialization is synchronous and performs no network or disk I/O; callers choose
an appropriate dispatcher for large payloads. Invalid JSON or incompatible data
propagates to the caller, and a decoded null result is rejected.

`NetworkModule` registers a single `NetworkComponents` holder, which keeps Gson
inside `impl`. Its configured Gson instance is injected into both `HttpClientFactory`
and `GsonJsonCodec`. The builder is used only to construct that shared instance.

`HttpClientFactory` is internal to `impl`. DI uses composition functions that return
network API contracts and share the factory through `NetworkComponents`.

## Connectivity observation

`NetworkConnectivityProvider.connectivity` exposes a StateFlow. The former
`isInternetAvailable` LiveData contract has been removed; consumers collect connectivity
or adapt it at their own lifecycle boundary. `AppApplication` calls `start()` explicitly after
loading DI; resolving the provider does not register Android network callbacks.
Repeated calls to `start()` do not duplicate registration.

The provider follows the default network and reports ONLINE only when Android marks
it validated. This includes supported default transports beyond Wi-Fi and cellular.
Updates are immediate, without debounce. ONLINE
does not guarantee that an individual API or host is reachable. Observation lives
for the application lifetime and retains no Activity.

The initial state is a snapshot of the current default network, refreshed by `start()`
during startup after callback registration. It is not hardcoded to OFFLINE: an already validated
network starts ONLINE. OFFLINE means no validated default network was found, including
when Android is still validating a connection or a captive portal is present. Consumers
should use this state as a connectivity hint rather than block requests or discard data.
Callbacks consume the supplied capabilities instead of querying synchronous network
snapshots; losing an obsolete network does not overwrite the replacement network state.
