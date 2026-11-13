# Gate de revisão do CodeRabbit

## Objetivo

O CodeRabbit revisa PRs novos e atualizados, inclusive PRs `task` destinados à
branch de uma feature. A configuração em `.coderabbit.yaml` inclui rascunhos,
publica o check `CodeRabbit`, trata falhas da revisão como falha do check e
habilita decisões automáticas de revisão. O autor não pode usar os comandos
`@coderabbitai approve` ou `@coderabbitai resolve` para aprovar o próprio PR.

## Configuração externa

1. Instale o aplicativo GitHub do CodeRabbit somente para este repositório e
   confira as permissões solicitadas. A instalação precisa permitir publicar
   checks, status e revisões em PRs. A aprovação automática exige um plano do
   CodeRabbit que ofereça esse recurso.
2. Abra um PR de validação e espere o check `CodeRabbit` aparecer. Confira que
   o autor do check é o aplicativo CodeRabbit.
3. No ruleset ativo de `main`, adicione `CodeRabbit` aos checks obrigatórios,
   selecionando o aplicativo CodeRabbit como origem. Preserve os checks já
   exigidos, a resolução obrigatória das conversas e a dispensa de aprovações
   antigas após novos commits.
4. Exija duas aprovações no ruleset: uma aprovação automática do CodeRabbit não
   deve substituir a aprovação humana já exigida. A contagem, por si só, não
   obriga que uma das aprovações venha de uma pessoa; confira as aprovações em
   cada PR. Não habilite essa contagem se não houver revisor humano disponível.
5. No painel do CodeRabbit, fixe `reviews.review_progress`,
   `reviews.commit_status` e `reviews.fail_commit_status` como substituições
   globais, se o plano permitir. Assim, uma alteração da configuração na branch
   do PR não pode enfraquecer o check exigido.

## Fluxo de revisão

- Um apontamento acionável deve gerar uma revisão com alterações solicitadas.
  Corrija o problema, envie um novo commit e aguarde a revisão desse commit.
  A resolução das conversas continua obrigatória no GitHub.
- Para um falso positivo, explique a decisão na conversa, peça a avaliação de
  um mantenedor e resolva a conversa de forma visível. O autor não deve usar
  comandos de aprovação ou resolução em massa no próprio PR.
- Se a revisão falhar, consulte o check e solicite `@coderabbitai full review`.
  Falhas recorrentes exigem decisão explícita do mantenedor sobre o ruleset;
  qualquer exceção deve ficar registrada no PR e ser restaurada depois.
- Se o check estiver ausente, confirme a instalação e as permissões do app e
  solicite uma revisão completa. Um check ausente permanece pendente quando é
  obrigatório. Revisões ignoradas ou limitadas por cota podem aparecer como
  aprovadas no check; nesses casos, o revisor humano deve verificar a causa
  antes do merge.

## Validação da instalação

Use PRs de teste com e sem apontamentos acionáveis. Confirme que a revisão roda
na abertura e após novo commit; que alterações solicitadas e conversas abertas
bloqueiam o merge; e que a correção seguida de nova revisão libera o bloqueio.
Repita a verificação com revisão falha e sem check publicado. A configuração
local não substitui a instalação do app nem o ruleset do GitHub.

Referências: [check do CodeRabbit](https://docs.coderabbit.ai/pr-reviews/status-check),
[aprovações automáticas](https://docs.coderabbit.ai/pr-reviews/automatic-approvals),
[políticas de aprovação](https://docs.coderabbit.ai/pr-reviews/approval-policies).
