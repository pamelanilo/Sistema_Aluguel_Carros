# Roteiro de Testes Manuais

Execute com o backend e o frontend ativos e um banco Supabase configurado. Crie ao menos um agente de empresa e um agente bancário (ver README, seção "Criar agentes") antes dos testes de agente.

| ID | Perfil | Ação | Resultado esperado |
|---|---|---|---|
| T01 | Visitante | Abrir `/auth/me` sem sessão | HTTP 401 |
| T02 | Visitante | Cadastrar cliente com dados válidos | Cliente salvo; senha não deve ser retornada |
| T03 | Visitante | Cadastrar cliente com quatro rendimentos | Cadastro rejeitado |
| T04 | Cliente | Login com credenciais válidas | Sessão iniciada e perfil identificado |
| T05 | Cliente | Listar automóveis | Lista de automóveis cadastrados |
| T06 | Cliente | Criar pedido com data final posterior à inicial | Pedido criado com status `PENDENTE` |
| T07 | Cliente | Consultar pedidos | Somente pedidos do próprio cliente |
| T08 | Cliente | Alterar datas de pedido pendente | Dados atualizados |
| T09 | Cliente | Cancelar pedido pendente | Status `CANCELADO` |
| T10 | Cliente | Tentar consultar pedido de outro cliente | Operação negada |
| T11 | Agente empresa | Consultar pedidos | Lista de pedidos recebidos |
| T12 | Agente empresa | Aprovar/reprovar pedido com parecer | Status e parecer registrados |
| T13 | Agente empresa | Gerar contrato para pedido aprovado | Contrato de aluguel criado |
| T14 | Agente empresa | Gerar contrato para pedido não aprovado | Operação rejeitada |
| T15 | Agente | Cadastrar automóvel | Automóvel registrado com proprietário |
| T16 | Agente | Consultar, editar e excluir cliente | CRUD executado |
| T17 | Agente banco | Criar crédito vinculado a contrato | Contrato de crédito registrado |
| T18 | Agente empresa | Tentar criar crédito como se fosse banco | Operação rejeitada |
| T19 | Agente | Aprovar pedido ainda PENDENTE | Rejeitado: é preciso iniciar a análise |
| T20 | Agente | Iniciar análise e recusar sem justificativa | Recusa rejeitada |
| T21 | Agente | Recusar com justificativa | Status Recusado; cliente vê a justificativa |
| T22 | Agente | Gerar contrato `PROPRIEDADE_CLIENTE` | Contrato EXECUTADO; proprietário do automóvel = cliente |
| T23 | Visitante | POST `/agentes/bancos` sem `X-Setup-Token` | HTTP 403 |
| T24 | Visitante | Cadastrar cliente com CPF inválido ou repetido | Cadastro rejeitado com mensagem clara |
| T25 | Cliente | Abrir a tela **Contratos** após ter pedido aprovado com contrato | Vê somente o próprio contrato (e crédito, se houver), sem botões de ação |
| T26 | Cliente | `GET /contratos/aluguel` | Lista apenas contratos dos seus pedidos |
| T27 | Cliente | `POST /contratos/aluguel` | HTTP 403 (somente agente gera contrato) |
| T28 | Cliente | Abrir `#gestao-clientes` na URL | Volta ao Início; telas de gestão não abrem |
| T29 | Qualquer | Atualizar a página (F5) logado | Sessão e tela atual são mantidas |
| T30 | Visitante | Digitar CPF e placa nos formulários | Máscaras aplicadas (000.000.000-00 / ABC1D23) |

## Antes da entrega
- Confirmar que nenhuma senha está no código ou em `.env.example`.
- Executar o build do frontend (`npm run build`).
- Executar os testes do backend com JDK 25 (`./mvnw test` ou `.\mvnw.cmd test`): os testes unitários rodam sem banco; o teste de contexto só roda com `SUPABASE_DB_URL` definida.
- Conferir os endpoints no navegador/DevTools e confirmar que a sessão persiste após atualizar a página.
- Regenerar os PNGs dos diagramas usando os arquivos `.puml` atualizados.
