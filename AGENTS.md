# Diretrizes do repositório

Projeto Android modular que usa Kotlin, Jetpack Compose e injeção de dependências em tempo de execução.

## Arquitetura e módulos

- `app/` inicia o aplicativo. Mantenha todas as chamadas estruturais de inicialização e o carregamento dos módulos de DI explicitamente em `AppApplication`; factories e registros de DI não devem iniciar tarefas em segundo plano nem inicializar SDKs implicitamente.
- `features/<feature>/` contém os módulos de apresentação, domínio, dados, analytics e DI necessários. Cada feature tem seu próprio módulo `analytics`. Os contratos de repositório ficam em `domain/api`; implementações e fontes de dados ficam em um único módulo `data`. O domínio não depende de persistência nem de SDKs de fornecedores.
- Cada feature tem um único módulo `di`, sem divisão entre `api` e `impl`. Declare os bindings exclusivamente nele; a apresentação apenas consome dependências. Contratos de navegação entre features ficam em `structural/navigation/api`; o DI de cada feature registra entry points Compose no registro compartilhado e uma feature registra o destino inicial. Cada feature é dona do seu `NavHost` Compose e da backstack interna; o `NavHost` do app monta seus destinos a partir dos entry points registrados e mantém a backstack entre features. A Activity launcher permanece apenas como host de `setContent`; não crie Activities, Fragments, layouts XML ou grafos XML de navegação para telas de features. Não crie módulos Gradle de navegação por feature.
- `structural/<name>/` contém serviços compartilhados com módulos `api`, `impl` e `di`. `core` e `design-system` mantêm seus módulos principais ao lado de `di`.
- Use Gradle `implementation` por padrão; use `api` somente para contratos que devam ser reexportados intencionalmente. Mantenha callbacks de UI e modelos de apresentação em Composables, sem expô-los por estado de ViewModel. Telas de feature seguem MVI: ViewModels expõem `uiState` por `StateFlow`, recebem `UiEvent` por `sendEvent` e expõem `uiEffect` por `Flow`, emitido por `sendEffect`; `sendEvent` deve sobrescrever o contrato de `BaseViewModel`. Classes de tela herdam de `BaseScreen`, cujo método `Start()` coleta automaticamente `uiState` e `uiEffect`, e sobrescrevem `Content(uiState)` e `processEffect`.
- Mantenha todas as dependências e tipos de fornecedores de DI dentro de `structural/di/impl`. Consumidores usam os contratos de `di/api` para registro, qualificadores, parâmetros e resolução. O serviço fundamental `di` tem apenas `api` e `impl`, sem um módulo próprio de autorregistro. Contratos de ViewModel do AndroidX e helpers de delegação Compose ficam em `di/api`; suas implementações ficam em `di/impl`. O design-system depende apenas da API, nunca da implementação.
- As camadas das features dependem de contratos: domínio depende das APIs de domínio, e apresentação depende das APIs de domínio, não de dados nem de implementações de domínio. Serviços estruturais compartilhados são consumidos por suas APIs. O app é a raiz de composição e pode acessar todas as camadas; módulos DI estruturais conectam suas próprias implementações. Mantenha implementações internas e evite dependências circulares. Nem `api` nem `impl` dependem de `di`.
- APIs estruturais expõem suas próprias interfaces e modelos, preferencialmente em Kotlin e com coroutines. Represente resultados e atualizações assíncronas públicas com `Flow`, em vez de parâmetros de callback; mantenha callbacks de plataforma dentro de `impl`. Encapsule tipos de plataforma e SDKs em `impl`; componentes visuais Compose e adaptadores do ciclo de vida da tela pertencem a `design-system`.
- Antes de usar diretamente uma API de plataforma ou biblioteca de terceiros, verifique se um serviço estrutural existente é responsável por ela. Consuma o contrato da API; se faltar uma capacidade necessária, amplie o serviço estrutural em vez de duplicar o comportamento.
- Declare dependências opcionais somente nos consumidores que precisam delas; não as imponha a classes-base. Nunca mantenha uma Activity em um singleton. Injete dispatchers nos componentes que executam trabalho assíncrono.
- Nunca coloque clientes de bibliotecas, SDKs de fornecedores ou adaptadores de integração em `core`. Crie um serviço estrutural dedicado com `api`, `impl` e `di` sempre que uma integração desse tipo for necessária; mantenha os detalhes da biblioteca em `impl`.
- `build-logic/` contém os plugins de convenção; versões e coordenadas de dependências pertencem a `gradle/libs.versions.toml`.

## Documentação

- Escreva KDoc em inglês. Todo arquivo criado sob `/docs` deve ser escrito em pt-BR. Este `AGENTS.md` também é mantido em pt-BR.
- Cada serviço estrutural tem um `README.md` na raiz que descreve sua responsabilidade, DI, uso e comportamentos relevantes. Detalhes de implementação dos serviços pertencem a esses READMEs, não a este arquivo.
- Interfaces estruturais públicas exigem KDoc. Documente comportamentos e condições que não sejam evidentes pela assinatura; não repita nomes de constantes nem descrições de getters e setters simples.
- Features, incluindo seus módulos `di`, não exigem KDoc. Atualize a documentação afetada junto com as mudanças de código.

## Código e organização

- Use quatro espaços e a formatação do Android Studio, seguindo o código existente.
- Não use aliases em imports Kotlin; resolva colisões de nomes renomeando ou qualificando as declarações no código.
- Componentes reutilizáveis do design system devem terminar com o sufixo `Sample`.
- Em features Compose, resolva cada ViewModel no `NavHost` da feature e passe-o como parâmetro para a tela. Cada tela deve ter um `@Preview` próprio, usando conteúdo de preview sem depender de DI.
- Organize o módulo de apresentação por tela, com pacotes próprios como `<tela>.model`, `<tela>.screen` e `<tela>.viewModel`. Mantenha `UiState`, `UiEvent` e `UiEffect` no pacote `model` da tela; não declare esses tipos junto ao ViewModel. As telas herdam de `BaseScreen` passando o ViewModel no construtor e implementam `Content(uiState)` e `processEffect`; o `NavHost` chama `Start()`, que coleta automaticamente `uiState` e `uiEffect`, encaminhando os efeitos para `processEffect`.
- Centralize dimensões de interface (como tamanhos, espaçamentos, raios, espessuras de traço e tamanhos de texto) nos arquivos de tokens de dimensões do design system, organizadas por categoria e com nomes reutilizáveis. Componentes e telas devem consumir esses tokens em vez de declarar valores de dimensão diretamente; adicione novos tokens genéricos quando necessário.
- Cada módulo DI estrutural declara seus registros em uma propriedade privada `<service>Module = dependencyModule { ... }` dentro do objeto do módulo. O método `init()` usa uma chamada de linha única `loadModules(serviceModule)`. Chamadas que carregam vários módulos de feature mantêm um argumento por linha.
- Use PascalCase para tipos, camelCase para membros, SNAKE_CASE para constantes e snake_case para recursos Android. Preserve sufixos como `ViewModel`, `Repository`, `UseCase` e `Impl`; use `DTO` para objetos de transferência e `Entity` para entidades no pacote `entity`.
- Em classes, deixe uma linha em branco no início e no fim do corpo e separe os membros com linhas em branco. Em interfaces e sealed interfaces, deixe uma linha em branco apenas no início e no fim do corpo, sem linhas em branco entre métodos ou declarações. Enums não têm linhas em branco junto às chaves.
- Prefira nomes claros a comentários que apenas descrevam o código. Nomeie exceções não utilizadas como `_`.
- Não crie módulos Gradle nem diretórios vazios. Isso inclui diretórios chamados `api`, `impl` ou `di`: crie-os apenas quando tiverem conteúdo necessário e remova-os se ficarem vazios.
- Adicione um `.gitignore` a cada novo módulo Gradle.
- Todo campo de DTOs HTTP de requisição e resposta, inclusive DTOs aninhados, deve declarar `@JsonName("server_field_name")` de `network/api`. Nunca deduza nomes do protocolo a partir dos nomes das propriedades Kotlin; os mapeamentos precisam continuar válidos após ofuscação.
- Fontes Kotlin ficam em `src/main/kotlin/` e `src/<sourceSet>/kotlin/` para outras variantes. Recursos Android não visuais, como manifests, ícones e temas de plataforma, ficam em `src/main/res/`; telas são implementadas em Kotlin com Compose, sem layouts XML. Testes Kotlin locais ficam em `src/test/kotlin/`.
- Ao mover ou excluir arquivos, remova diretórios que ficarem vazios, incluindo ancestrais vazios.

## Build e validação

Use o Gradle Wrapper com JDK 17 e Android SDK 36. Configure o SDK pelo Android Studio ou por `local.properties`. Os product flavors são `dev` e `prd`.

- `./gradlew :app:assembleDevDebug`: compila o APK.
- `./gradlew :app:installDevDebug`: instala em um dispositivo ou emulador.
- `./gradlew :app:testDevDebugUnitTest`: executa os testes locais do app.
- `./gradlew :app:lintDevDebug`: executa o Android Lint.

Execute verificações proporcionais à mudança e aos testes existentes nos módulos afetados. Use JUnit 4, o sufixo `Test` nas classes e nomes descritivos para os métodos. Não crie testes instrumentados. Nesta etapa, não adicione novos testes unitários, a menos que o usuário solicite.

## Commits e pull requests

- Crie commits somente quando solicitado. Revise todas as mudanças pendentes, inclusive arquivos novos, e separe objetivos independentes em commits coerentes.
- Use mensagens curtas em inglês no formato `type: description`, sem escopo. Exemplos: `feat: add analytics provider` e `refactor: simplify dependency injection`.
- Use tipos semânticos como `feat`, `fix`, `refactor`, `docs`, `test`, `build`, `ci`, `perf`, `style` e `chore`.
- Para mudanças incompatíveis, adicione `!` após o tipo e um rodapé `BREAKING CHANGE:`.
- Em pull requests, descreva o comportamento, os módulos afetados e a validação; vincule issues e inclua capturas de tela para mudanças visuais.
