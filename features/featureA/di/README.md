# DI da featureA

Centraliza os registros de DI da feature, sem módulos `api` e `impl`. O app depende de `:features:featureA:di` e chama `FeatureAModule.init(navigationRegistry = get())` durante a inicialização, depois de carregar os módulos estruturais de DI.

Consumidores usam os contratos da feature diretamente; não há uma interface intermediária para carregar DI. Os bindings ficam neste módulo. Apresentação, domínio e dados não dependem dele.

Este módulo também registra `FeatureAAnalytics`, definido em `../analytics`, para injeção nas camadas da feature. Carregue `AnalyticsModule.init()` antes de resolver esse contrato.

Carregue `NavigationModule.init()` primeiro. Esta feature registra seu entry point Compose no registro compartilhado. Seu `NavHost` e as rotas internas ficam na apresentação, e as telas seguem MVI com estado, eventos e efeitos de uso único.
