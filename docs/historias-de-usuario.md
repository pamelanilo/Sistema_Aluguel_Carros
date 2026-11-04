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

**HU14 — Avaliar pedido**  
Como agente, quero registrar um parecer financeiro e aprovar ou reprovar o pedido, para comunicar o resultado da análise.

## Contratos
**HU15 — Gerar contrato de aluguel**  
Como agente, quero gerar um contrato de aluguel para um pedido aprovado.

**HU16 — Registrar contrato de crédito**  
Como agente bancário, quero associar um contrato de crédito a um contrato de aluguel, quando o cliente solicitar financiamento.

**HU17 — Consultar contratos**  
Como agente autorizado, quero consultar contratos de aluguel e de crédito registrados.

**HU18 — Consultar meus contratos**  
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

## Observações de escopo
- O modelo contém dados para contrato de crédito, mas o fluxo completo de solicitação de crédito pelo cliente e a comunicação automática de recusa ainda precisam de uma interface/etapa específica.
- O sistema armazena datas do pedido, mas o enunciado fornecido não define tarifa diária; por isso o projeto não inventa uma regra de cálculo de preço.
- As imagens PNG dos diagramas devem ser regeneradas a partir dos arquivos `.puml` após alterações na modelagem.

**HU18 — Iniciar análise financeira**  
Como agente, quero mover um pedido pendente para "Em análise" para sinalizar ao cliente que a avaliação financeira começou.

**HU19 — Recusar com justificativa**  
Como agente, quero registrar uma justificativa obrigatória ao recusar um pedido para que o cliente entenda o motivo.

**HU20 — Atualizar proprietário pelo contrato**  
Como agente, quero que o contrato de aluguel atualize o proprietário do automóvel e fique como "executado".
