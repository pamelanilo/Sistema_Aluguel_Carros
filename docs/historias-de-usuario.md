# Histórias de Usuário — VRUM!

## Atores
- **Cliente:** solicita e acompanha aluguéis.
- **Agente da empresa:** administra automóveis, consulta e avalia pedidos e trata contratos de aluguel.
- **Agente do banco:** realiza análise/ações relacionadas ao crédito e registra contratos de crédito.

## Cadastro e autenticação
**HU01 — Cadastrar cliente**  
Como visitante, quero cadastrar meus dados pessoais e rendimentos para criar uma conta no sistema.

**HU02 — Autenticar-se**  
Como usuário cadastrado, quero entrar com login e senha para acessar as funcionalidades autorizadas ao meu perfil.

**HU03 — Encerrar sessão**  
Como usuário autenticado, quero sair do sistema para encerrar minha sessão.

**HU21 — Cadastrar-se como empresa ou banco**  
Como agente (locadora ou banco), quero criar minha conta escolhendo o tipo na tela de cadastro e informando CNPJ e razão social (empresa) ou código do banco, para poder analisar pedidos.

**HU22 — Usar o modo escuro**  
Como usuário, quero alternar entre modo claro e escuro (ou seguir o tema do sistema) para usar o sistema com conforto visual.

**HU20 — Manter meus dados**  
Como cliente, quero consultar e alterar meus dados de identificação, profissão e rendimentos (até três), para que a análise financeira use informações atualizadas.

## Clientes
**HU04 — Consultar clientes**  
Como agente, quero consultar os dados de clientes para analisar os pedidos recebidos.

**HU05 — Manter cadastro de clientes**  
Como agente autorizado, quero cadastrar, alterar e excluir registros de clientes para manter os dados atualizados.

## Automóveis
**HU06 — Consultar automóveis**  
Como cliente autenticado, quero visualizar os automóveis disponíveis para escolher um para locação.

**HU07 — Registrar automóvel**  
Como agente, quero registrar matrícula, ano, marca, modelo, placa e proprietário do automóvel.

**HU08 — Manter automóveis**  
Como agente, quero alterar ou excluir os dados de automóveis cadastrados.

## Pedidos de aluguel
**HU09 — Introduzir pedido**  
Como cliente, quero selecionar um automóvel e informar as datas de retirada e devolução para solicitar o aluguel.

**HU10 — Consultar pedido**  
Como cliente, quero consultar meus pedidos e seus status para acompanhar a solicitação.

**HU11 — Modificar pedido**  
Como cliente, quero modificar as datas de um pedido pendente ou em análise para corrigir os dados da solicitação.

**HU12 — Cancelar pedido**  
Como cliente, quero cancelar um pedido pendente ou em análise quando desistir da contratação.

**HU13 — Consultar pedidos recebidos**  
Como agente, quero consultar os pedidos recebidos para realizar a análise.

**HU14 — Iniciar análise financeira**  
Como agente, quero mover um pedido pendente para "Em análise" e ver a profissão e a renda mensal do cliente, para sinalizar que a avaliação financeira começou e ter os dados necessários para ela.

**HU15 — Avaliar pedido**  
Como agente, quero registrar um parecer financeiro e aprovar ou reprovar o pedido (justificativa obrigatória na recusa), para comunicar o resultado da análise.

## Contratos
**HU16 — Gerar contrato de aluguel**  
Como agente, quero gerar um contrato de aluguel para um pedido aprovado, escolhendo o tipo (propriedade do cliente, da empresa ou do banco), para que o automóvel seja registrado com o proprietário correto e o contrato fique como "executado".

**HU17 — Registrar contrato de crédito**  
Como agente bancário, quero associar um contrato de crédito a um contrato de aluguel, quando o cliente solicitar financiamento.

**HU18 — Consultar contratos**  
Como agente autorizado, quero consultar contratos de aluguel e de crédito registrados.

**HU19 — Consultar meus contratos**  
Como cliente, quero consultar os contratos de aluguel (e o crédito, se houver) gerados a partir dos meus pedidos aprovados, sem ver os de outros clientes.

## Regras e critérios de aceite
1. As rotas privadas exigem autenticação.
2. O cliente só pode consultar, alterar ou cancelar os próprios pedidos.
3. A alteração e o cancelamento são permitidos apenas enquanto o pedido estiver `PENDENTE` ou `EM_ANALISE`.
4. A avaliação registra parecer obrigatório e define `APROVADO` ou `REPROVADO`.
5. Apenas pedidos aprovados podem gerar contrato de aluguel.
6. O cliente consulta apenas contratos dos próprios pedidos; só agentes geram contratos.
7. O contrato de crédito é opcional e somente pode ser registrado por um agente bancário autenticado.
8. Cada cliente pode informar até três rendimentos.
9. O proprietário do automóvel é uma instância de `Usuario`, podendo ser cliente, agente de empresa ou agente de banco.
10. Um pedido só pode ser aprovado se o automóvel não tiver outro pedido aprovado em período sobreposto.
11. Clientes e automóveis que já possuem pedidos não podem ser excluídos (preserva o histórico).
12. O pedido só pode ser analisado após `PENDENTE → EM_ANALISE`; a aprovação/recusa só ocorre a partir de `EM_ANALISE`.
13. O login é único entre clientes, empresas e bancos; CPF e CNPJ têm dígitos verificadores validados e não se repetem.
14. O autocadastro de empresas e bancos pode ser desligado por configuração (`APP_CADASTRO_AGENTES_PUBLICO=false`).

## Observações de escopo
- O crédito é concedido pelo agente bancário no momento de gerar o contrato; o cliente não solicita crédito por uma tela própria.
- O sistema armazena datas do pedido, mas o enunciado fornecido não define tarifa diária; por isso o projeto não inventa uma regra de cálculo de preço.
- As imagens PNG dos diagramas são geradas a partir dos arquivos `.puml`; versões anteriores ficam em `docs/diagramas/versoes/`.
