# Design system

Fornece componentes reutilizáveis em Jetpack Compose e o tema visual usado pelos módulos de apresentação das features. Os componentes de UI são implementados em Kotlin e não encapsulam layouts XML nem Views do Android.

## Componentes Compose

`ComposeSampleTheme` seleciona `ComposeSampleColors` para modo claro ou escuro e expõe a paleta pela API `CurrentTheme.color`, sem exigir que consumidores consultem `MaterialTheme` diretamente. `SampleScreen` fornece a estrutura de tela com edge to edge e aplica somente os insets do `Scaffold`, das barras de navegação e do teclado por `navigationBarsPadding` e `imePadding`. Seu `LoadingState` oferece `ONLY_LOADING`, `LOADING_DIM_BG` e `NONE`. `BaseScreen` recebe um `UiViewModel`; o `NavHost` inicia a tela com `Init()`, que coleta automaticamente o `StateFlow` `uiState` e o `Flow` `uiEffect`, chama `consumeEffect` para cada efeito e entrega o estado ao méstream `Content(uiState)` sobrescrito pela tela. `SampleCircularProgressIndicator` encapsula o indicador Material e usa as dimensões e espessura do tema. `SampleButton` aceita estado de carregamento, `SampleTextField` aplica o comportamento de entrada de texto do exemplo e `SampleDialog` apresenta um diálogo de confirmação padrão.

Os módulos de apresentação consomem esses componentes por `:structural:design-system`. O estado e os callbacks da tela permanecem na feature; componentes reutilizáveis recebem valores e lambdas de eventos e não dependem de ViewModels de feature.

## Adaptador de ciclo de vida biométrico

O módulo também fornece `LifecycleBiometricProvider`, que cancela uma solicitação biométrica em andamento quando o escopo de coroutine da tela é cancelado. O binding fica em `:structural:design-system:di`; telas que usam biometria solicitam um provider com a `FragmentActivity` e o escopo de ciclo de vida da tela. A Activity da plataforma serve apenas como adaptador exigido pelo AndroidX BiometricPrompt, não como implementação de tela.

O app carrega `DesignSystemModule.init()` depois do módulo DI de biometria durante a inicialização. O design-system consome contratos estruturais e não inicializa providers nem SDKs por conta própria.


## Textos e localização

Textos estáticos, incluindo acessibilidade e previews, vêm de string resources.
ViewModels expõem mensagens com `UiText` de `core`, sem resolver recursos ou reter
Context. A extensão composable `UiText.asString()` resolve `Raw` e
`StringResource(id, vararg params)` na configuração atual do Compose. Parâmetros
seguem a formatação posicional de resources (`%1$s`, `%2$d`). `Raw` é reservado a
conteúdo dinâmico, como texto digitado pelo usuário.

## Stream components

SampleSearchBar uses search/clear icons and a keyboard search action. SampleMediaCard displays artwork and optional metadata. SampleImage renders explicit `Loading`, `Loaded`, and `Unavailable` states. The loading UI is isolated so it can evolve into a shimmer without changing cache or decoding behavior. Consumers supply the `unavailableContent` composable slot to choose their unavailable UI; by default, that state renders no foreground content and triggers no action. Its internal state producer observes assets-cache, preserves the previously loaded image, and decodes local media on the injected IO dispatcher. SampleVideoPlayer is the visual/lifecycle adapter for video-player embeds, uses a 16:9 player, opens fullscreen playback in landscape, and destroys the WebView when leaving the screen. SampleConnectionBanner is rendered once by the app host when offline, above navigation. All static copy is in resources, including pt-BR translations.

ComposeSampleTheme now provides one shared Material 3 color scheme to MaterialTheme and CurrentTheme for both light and dark mode, including system bar contrast. SampleClearFocusOnOutsideTap is installed once in the app host and observes taps without consuming child clicks or scrolling gestures. It leaves taps inside the focused field intact.

## Tipografia

Consuma os estilos por `CurrentTheme.typography`, como `SampleText(text = texto, style = CurrentTheme.typography.labelMedium)`. `TypographyToken` tem construção e estilo Compose internos; os consumidores não acessam `TextStyle` nem a tipografia do Material. Fonte, peso, tamanho, altura de linha e espaçamento são definidos nos tokens internos do design system.

## Tamanhos

`CurrentTheme.size` expõe `xxxSmall`, `xxSmall`, `xSmall`, `small`, `medium`, `large`, `xLarge`, `xxLarge`, `xxxLarge` e `huge`. Os nomes públicos representam posições relativas na escala; os valores em dp ficam internos e podem ser remapeados sem alterar os consumidores. A escala padrão usa 4, 8, 12, 16, 20, 24, 32, 40, 48 e 64 dp, respectivamente. Tamanhos específicos fora dessa escala devem ser definidos na feature consumidora.
