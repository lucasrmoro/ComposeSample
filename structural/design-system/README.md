# Design system

Provides reusable Jetpack Compose components and the visual theme used by feature presentation modules. UI components are implemented in Kotlin and do not wrap XML layouts or Android Views.

## Compose components

`ComposeSampleTheme` selects `ComposeSampleColors` for light or dark mode and exposes the palette through `CurrentTheme.color`, so consumers do not need to access `MaterialTheme` directly. `SampleScreen` provides an edge-to-edge screen structure and applies only the `Scaffold`, navigation bar, and keyboard insets through `navigationBarsPadding` and `imePadding`. Its `LoadingState` supports `ONLY_LOADING`, `LOADING_DIM_BG`, and `NONE`. `BaseScreen` receives a `UiViewModel`; the `NavHost` starts the screen with `Init()`, which automatically collects the `uiState` `StateFlow` and `uiEffect` `Flow`, calls `consumeEffect` for each effect, and passes state to the screen's overridden `Content(uiState)` method. `SampleCircularProgressIndicator` wraps the Material indicator and uses theme dimensions and stroke width. `SampleButton` supports a loading state, `SampleTextField` applies the sample text-entry behavior, and `SampleDialog` displays a standard confirmation dialog. `SampleSurface`, `SampleDropdownMenu`, `SampleDropdownMenuItem`, and `SampleDivider` provide generic building blocks for feature-specific components.

Presentation modules consume these components through `:structural:design-system`. Screen state and callbacks remain in the feature; reusable components receive values and event lambdas and do not depend on feature ViewModels.

## Biometric lifecycle adapter

The module also provides `LifecycleBiometricProvider`, which cancels an ongoing biometric request when the screen coroutine scope is cancelled. Its binding lives in `:structural:design-system:di`; screens that use biometrics request a provider with the `FragmentActivity` and the screen lifecycle scope. The platform Activity is only an adapter required by AndroidX BiometricPrompt, not a screen implementation.

The app loads `DesignSystemModule.init()` after the biometric DI module during startup. The design system consumes structural contracts and does not initialize providers or SDKs on its own.

## Text and localization

Static text, including accessibility labels, comes from string resources. ViewModels expose messages through `UiText` from `core` without resolving resources or retaining a `Context`. The composable `UiText.asString()` extension resolves `Raw` and `StringResource(id, vararg params)` in the current Compose configuration. Parameters follow positional resource formatting (`%1$s`, `%2$d`). `Raw` is reserved for dynamic content, such as user-entered text.

## Shared Compose components

`SampleSearchBar` uses search and clear icons and a keyboard search action. `SampleImage` renders explicit `Loading`, `Loaded`, and `Unavailable` states. Consumers select its loading appearance with `SampleImageLoadingStyle.DEFAULT` or `SHIMMER`. The internal `SampleShimmer` animates a diagonal gradient from top-left to bottom-right using Jetpack Compose drawing and animation APIs, with colors resolved through the theme. Animation progress is read during drawing rather than recomposition; no third-party shimmer dependency or implementation types are exposed to consumers. Consumers provide the `unavailableContent` composable slot to choose unavailable UI; by default, that state renders no foreground content and triggers no action. Its internal state producer observes assets-cache, preserves the previously loaded image, and decodes local media on the injected IO dispatcher. `SampleVideoPlayer` is the visual and lifecycle adapter for video-player embeds, uses a 16:9 player, opens fullscreen playback in landscape, and destroys the WebView when leaving the screen. `SampleConnectionBanner` is rendered once by the app host when offline, above navigation. Production UI copy is in resources, including pt-BR translations. Previews and preview-only helpers use literal sample data without dedicated string resources.

`ComposeSampleTheme` provides one shared Material 3 color scheme to `MaterialTheme` and `CurrentTheme` in light and dark modes, including system bar contrast. `SampleClearFocusOnOutsideTap` is installed once in the app host and observes taps without consuming child clicks or scrolling gestures. It leaves taps inside the focused field intact.

## Typography

Consume styles through `CurrentTheme.typography`, for example `SampleText(text = text, style = CurrentTheme.typography.labelMedium)`. `TypographyToken` keeps Compose construction and styling internal; consumers do not access `TextStyle` or Material typography. Font, weight, size, line height, and letter spacing are defined in the design system's internal tokens.

## Sizes

`CurrentTheme.size` exposes `xxxSmall`, `xxSmall`, `xSmall`, `small`, `medium`, `large`, `xLarge`, `xxLarge`, `xxxLarge`, and `huge`. These public names represent relative positions on the scale; dp values remain internal and can be remapped without changing consumers. The default scale uses 4, 8, 12, 16, 20, 24, 32, 40, 48, and 64 dp, respectively. Feature consumers should define specific sizes outside this scale.

## API conventions

Public components receive `modifier` first and pass it to their visual root. `SampleProfileImage` follows the naming prefix used by other components; the opaque color token is `ColorToken`, used by `SampleIcon` without exposing its implementation.
