# Limitações conhecidas

## Verificação feita na revisão
- **Frontend:** `npm run build` executado com sucesso. Os fluxos de login, cadastro, catálogo, pedido, análise, aprovação/recusa, contrato, crédito, contratos do cliente e responsividade foram exercitados em navegador (Chromium) contra um **servidor simulado** (mock) com as mesmas rotas da API. Isso valida a interface, **não** o backend Spring.
- **Backend:** **não foi compilado nem testado** no ambiente da revisão (Java 21 disponível, JDK 25 exigido; Maven Central inacessível). As alterações feitas no backend foram pequenas (consulta de contratos pelo cliente e testes unitários) e passaram apenas por checagem de sintaxe. **Rode `./mvnw test` e `./mvnw spring-boot:run` na sua máquina (JDK 25) antes de entregar.**
- Nenhum teste foi executado contra o Supabase real.

## Escopo
- Não há cálculo de preço/tarifa nem controle de disponibilidade do automóvel (não definidos no enunciado).
- Sem notificações por e-mail/SMS; o cliente acompanha o estado em "Meus pedidos".
- O cliente não edita os próprios dados: a manutenção de clientes é feita por agentes.
- Sessão por cookie (HttpSession); CSRF desabilitado por simplicidade acadêmica.
- Testes automatizados: unitários de modelo/DTO (sem banco) e `contextLoads` (apenas com banco configurado). Não há testes de serviço com mocks nem de controllers.
