# Navegação

Define os contratos de entrada das features e o registro que as conecta sem expor suas implementações de apresentação.

## Módulos

- `api/core`: destinos de feature, entry points Compose, registro de navegação e contrato do navegador entre features.
- `api/<feature>`: destinos tipados agrupados por feature, como `stream/StreamDestination`.
- `impl`: registro de rotas mantido durante a vida do app.
- `di`: registros de DI para o registro compartilhado de rotas.

O app monta um `NavHost` do Navigation Compose a partir dos entry points registrados e mantém a backstack entre features. Cada feature registrada fornece um entry point Compose que hospeda seu próprio `NavHost` e sua backstack interna. O DI da feature registra o entry point durante a inicialização do app; uma delas deve marcar seu entry point como destino inicial. A apresentação não usa Activity de feature nem grafo XML de navegação.

## Configuração

Carregue `NavigationModule.init()` em `AppApplication` antes de registrar os entry points das features. Passe o registro compartilhado ao DI da feature:

```kotlin
NavigationModule.init()
StreamModule.init(navigationRegistry = get())
```

O DI da feature registra `createStreamEntryPoint()` no registro. Para a feature inicial, passe `isStartDestination = true`. Nomes de rota duplicados e múltiplos destinos iniciais são rejeitados; montar o host sem um destino inicial falha com `IllegalStateException`.

## Uso

O app cria um destino do `NavHost` para cada entry point registrado e chama seu Composable `Content`. A feature pode solicitar outra feature por `LocalFeatureNavigator.current.navigate(destination)`. O navegador empilha uma nova entrada; abrir a mesma feature outra vez cria outra entrada. O Navigation Compose trata o botão voltar primeiro na navegação interna da feature ativa e, quando ela está no destino inicial, retorna à feature chamadora.

O `NavHost` do app salva sua backstack e o estado dos destinos, permitindo restaurar o `NavHost` interno e o estado salvável da tela quando uma feature filha é fechada. Não retenha Activity nem `NavController` no registro global do processo.

Declare destinos como implementações de `FeatureDestination` nesta API. Cada feature é dona das rotas internas e dos Composables. Não crie um módulo Gradle de navegação por feature.

Use a extensão `String.navArgument { ... }` de `api/core` para declarar argumentos nas rotas internas das features. Ela mantém o DSL oficial do Navigation Compose e permite manter o nome do argumento junto ao seu identificador compartilhado, por exemplo `Routes.Arguments.ID.navArgument { type = NavType.LongType }`.
