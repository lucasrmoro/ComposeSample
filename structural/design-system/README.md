# Design system

Fornece componentes reutilizáveis em Jetpack Compose e o tema visual usado pelos módulos de apresentação das features. Os componentes de UI são implementados em Kotlin e não encapsulam layouts XML nem Views do Android.

## Componentes Compose

`ComposeSampleTheme` seleciona `ComposeSampleColors` para modo claro ou escuro e expõe a paleta pela API `CurrentTheme.color`, sem exigir que consumidores consultem `MaterialTheme` diretamente. `ScreenSample` fornece a estrutura de tela com edge to edge e aplica somente os insets do `Scaffold`, das barras de navegação e do teclado por `navigationBarsPadding` e `imePadding`. Seu `LoadingState` oferece `ONLY_LOADING`, `LOADING_DIM_BG` e `NONE`. `BaseScreen` recebe um `UiViewModel`; o `NavHost` inicia a tela com `Start()`, que coleta automaticamente o `StateFlow` `uiState` e o `Flow` `uiEffect`, chama `processEffect` para cada efeito e entrega o estado ao método `Content(uiState)` sobrescrito pela tela. `CircularProgressIndicatorSample` encapsula o indicador Material e usa as dimensões e espessura do tema. `ButtonSample` aceita estado de carregamento, `TextFieldSample` aplica o comportamento de entrada de texto do exemplo e `DialogSample` apresenta um diálogo de confirmação padrão.

Os módulos de apresentação consomem esses componentes por `:structural:design-system`. O estado e os callbacks da tela permanecem na feature; componentes reutilizáveis recebem valores e lambdas de eventos e não dependem de ViewModels de feature.

## Adaptador de ciclo de vida biométrico

O módulo também fornece `LifecycleBiometricProvider`, que cancela uma solicitação biométrica em andamento quando o escopo de coroutine da tela é cancelado. O binding fica em `:structural:design-system:di`; telas que usam biometria solicitam um provider com a `FragmentActivity` e o escopo de ciclo de vida da tela. A Activity da plataforma serve apenas como adaptador exigido pelo AndroidX BiometricPrompt, não como implementação de tela.

O app carrega `DesignSystemModule.init()` depois do módulo DI de biometria durante a inicialização. O design-system consome contratos estruturais e não inicializa providers nem SDKs por conta própria.
