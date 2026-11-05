# API REST — VRUM!

Base local: `http://localhost:8080`. As rotas abaixo são chamadas pelo frontend com `credentials: include`, usando sessão HTTP.

| Método | Rota | Perfil | Descrição |
|---|---|---|---|
| POST | `/auth/login` | Público | Inicia sessão |
| GET | `/auth/me` | Autenticado | Retorna usuário da sessão |
| POST | `/auth/logout` | Autenticado | Encerra sessão |
| POST | `/clientes` | Público | Cadastra cliente (HTTP 201) |
| GET | `/clientes/me` | Cliente | Consulta os próprios dados |
| PUT | `/clientes/me` | Cliente | Altera os próprios dados e rendimentos (senha opcional) |
| GET | `/clientes` e `/clientes/{id}` | Agente | Consulta clientes |
| PUT | `/clientes/{id}` | Agente | Atualiza cliente |
| DELETE | `/clientes/{id}` | Agente | Exclui cliente sem pedidos (HTTP 204) |
| GET | `/automoveis` e `/automoveis/{id}` | Autenticado | Consulta automóveis |
| POST | `/automoveis` | Agente | Cadastra automóvel |
| PUT | `/automoveis/{id}` | Agente | Atualiza automóvel |
| DELETE | `/automoveis/{id}` | Agente | Exclui automóvel sem pedidos |
| POST | `/pedidos` | Cliente | Cria pedido |
| GET | `/pedidos` e `/pedidos/{id}` | Autenticado | Cliente vê os próprios; agente vê todos |
| PUT | `/pedidos/{id}` | Cliente dono ou agente | Atualiza pedido enquanto permitido |
| PATCH | `/pedidos/{id}/cancelar` | Cliente dono | Cancela pedido pendente/em análise |
| PATCH | `/pedidos/{id}/analisar` | Agente | PENDENTE → EM_ANALISE (inicia análise financeira) |
| PATCH | `/pedidos/{id}/avaliar` | Agente | Somente de EM_ANALISE: aprova/recusa com parecer (justificativa obrigatória). A aprovação exige o automóvel livre no período |
| GET | `/agentes/cadastro-publico` | Público | `{"habilitado": true}` se a tela pode cadastrar empresa/banco |
| POST | `/agentes/empresas` e `/agentes/bancos` | Público se `APP_CADASTRO_AGENTES_PUBLICO=true`; senão agente ou header `X-Setup-Token` | Cria empresa (CNPJ, razão social) ou banco (CNPJ, código de 3 dígitos). HTTP 201 |
| GET | `/agentes/empresas` e `/agentes/bancos` | Agente | Lista agentes |
| GET | `/health` | Público | Health check (`{"status":"UP"}`), usado pelo Render |
| POST | `/contratos/aluguel` | Agente | Cria contrato (estado EXECUTADO) para pedido aprovado e atualiza o proprietário do automóvel conforme o tipo; `PROPRIEDADE_BANCO` exige agente bancário |
| GET | `/contratos/aluguel` | Agente ou cliente | Agente: todos os contratos. Cliente: somente os contratos dos próprios pedidos |
| GET | `/contratos/aluguel/{id}` | Agente | Consulta um contrato de aluguel |
| POST | `/contratos/credito` | Agente bancário | Registra crédito do banco autenticado |
| GET | `/contratos/credito` | Agente ou cliente | Agente: todos os créditos. Cliente: somente os créditos dos próprios contratos |
| GET | `/contratos/credito/{id}` | Agente | Consulta um contrato de crédito |

## Códigos de resposta
Erros seguem o formato `ProblemDetail` (`{"status":400,"detail":"mensagem"}`):

| Código | Quando |
|---|---|
| 400 | Regra de negócio violada (datas inválidas, CPF inválido, mais de 3 rendimentos, estado do pedido não permite a ação, automóvel reservado…) |
| 401 | Sem sessão ou login/senha incorretos |
| 403 | Perfil sem permissão ou recurso de outro cliente |
| 404 | Recurso não encontrado |
| 409 | Violação de integridade (ex.: placa repetida) |

A resposta de pedido inclui, para a análise financeira, `clienteProfissao`, `clienteRendaTotal` e `clienteQtdRendimentos`.

## Exemplos de payload

### Login
```json
{ "login": "usuario", "senha": "senha" }
```

### Cadastro de cliente
```json
{
  "nome": "Nome do Cliente",
  "login": "cliente01",
  "senha": "senha-forte",
  "rg": "MG123456",
  "cpf": "12345678901",
  "endereco": "Rua Exemplo, 100",
  "profissao": "Analista",
  "rendimentos": [
    { "entidadeEmpregadora": "Empresa Exemplo", "valor": 3500.00 }
  ]
}
```

### Criar pedido
```json
{
  "automovelId": 1,
  "dataInicio": "2026-11-10",
  "dataFim": "2026-11-15"
}
```

### Avaliar pedido
```json
{
  "status": "APROVADO",
  "parecer": "Análise concluída; pedido aprovado."
}
```

### Criar automóvel
```json
{
  "matricula": "MAT-001",
  "ano": 2024,
  "marca": "Marca",
  "modelo": "Modelo",
  "placa": "ABC1D23",
  "proprietarioId": 1
}
```


## Cadastro de agentes
`POST /agentes/empresas` e `POST /agentes/bancos` recebem:
- Empresa: `nome`, `login`, `senha`, `cnpj`, `razaoSocial`.
- Banco: `nome`, `login`, `senha`, `cnpj`, `codigoBanco` (3 dígitos).

Validações: CNPJ com dígitos verificadores, CNPJ único, login único entre **todos** os usuários, senha com 6+ caracteres.

Com `APP_CADASTRO_AGENTES_PUBLICO=true` (padrão) qualquer visitante pode se cadastrar como empresa ou banco pela tela **Criar conta**. Em produção, recomenda-se `false`: os endpoints passam a exigir agente autenticado ou o header `X-Setup-Token` (valor de `APP_SETUP_TOKEN`).
