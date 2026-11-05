# Limitações conhecidas

## Verificação realizada
- **Backend:** compilado e testado com JDK 25 + Spring Boot 4.0.9 (`./mvnw test`: 8 testes, 7 executados e 1 ignorado — o `contextLoads`, que exige `SUPABASE_DB_URL`; inclui `CadastroAgentesRestritoTest`).
  - `FluxoAluguelIntegrationTest` exercita a API real (controllers → services → repositories → JPA) sobre **H2 em memória, modo PostgreSQL**: autenticação, CRUD de cliente, "meus dados", automóveis, pedido completo (criar, alterar, analisar, aprovar/recusar, cancelar), disponibilidade do automóvel, contrato de aluguel com troca de proprietário, contrato de crédito e permissões por perfil.
  - A aplicação também foi executada (`spring-boot:run`) e consumida pelo proxy do Vite com cookie de sessão real.
- **Frontend:** `npm run build` sem erros. Cadastro de cliente/empresa/banco, modo escuro (tema do sistema, alternância e persistência) e Design System sem login verificados no Chrome headless.
- **Deploy:** o jar gerado pelo mesmo comando do `Dockerfile` foi executado como no Render (`PORT`, cookie `Secure`, `X-Forwarded-Proto`) contra o Supabase pelo Transaction pooler. A imagem Docker **não** foi construída nesta máquina (Docker não instalado): o primeiro build acontece no Render.
- **Supabase:** aplicação executada conectada ao Supabase do projeto (tabelas criadas pelo Hibernate). Fluxo completo validado no banco real: cadastro, login, análise, bloqueio por automóvel reservado, recusa com justificativa, contrato de propriedade do banco (troca de proprietário) e crédito.

## Escopo
- Não há cálculo automático de preço/tarifa (não definido no enunciado); o valor do pedido é informado.
- A disponibilidade considera apenas pedidos **aprovados** em períodos sobrepostos.
- Sem notificações por e-mail/SMS; o cliente acompanha o estado em "Meus pedidos".
- Sessão por cookie (HttpSession); CSRF desabilitado por simplicidade acadêmica.
- Com `APP_CADASTRO_AGENTES_PUBLICO=true`, qualquer visitante pode se cadastrar como empresa ou banco (adequado ao protótipo; desligue em produção).
