# API REST — VRUM!

Base local: `http://localhost:8080`. As rotas abaixo são chamadas pelo frontend com `credentials: include`, usando sessão HTTP.

| Método | Rota | Perfil | Descrição |
|---|---|---|---|
| POST | `/auth/login` | Público | Inicia sessão |
| GET | `/auth/me` | Autenticado | Retorna usuário da sessão |
| POST | `/auth/logout` | Autenticado | Encerra sessão |
| POST | `/clientes` | Público | Cadastra cliente |
| GET | `/clientes` e `/clientes/{id}` | Agente | Consulta clientes |
| PUT | `/clientes/{id}` | Agente | Atualiza cliente |
| DELETE | `/clientes/{id}` | Agente | Exclui cliente |
| GET | `/automoveis` e `/automoveis/{id}` | Autenticado | Consulta automóveis |
| POST | `/automoveis` | Agente | Cadastra automóvel |
| PUT | `/automoveis/{id}` | Agente | Atualiza automóvel |
| DELETE | `/automoveis/{id}` | Agente | Exclui automóvel |
| POST | `/pedidos` | Cliente | Cria pedido |
| GET | `/pedidos` e `/pedidos/{id}` | Autenticado | Cliente vê os próprios; agente vê todos |
| PUT | `/pedidos/{id}` | Cliente dono ou agente | Atualiza pedido enquanto permitido |
| PATCH | `/pedidos/{id}/cancelar` | Cliente dono | Cancela pedido pendente/em análise |
| PATCH | `/pedidos/{id}/analisar` | Agente | PENDENTE → EM_ANALISE (inicia análise financeira) |
| PATCH | `/pedidos/{id}/avaliar` | Agente | Somente de EM_ANALISE: aprova/recusa com parecer (justificativa obrigatória) |
| POST | `/agentes/empresas` e `/agentes/bancos` | Agente ou header `X-Setup-Token` | Cria agentes (token = variável `APP_SETUP_TOKEN`) |
| POST | `/contratos/aluguel` | Agente | Cria contrato (estado EXECUTADO) para pedido aprovado e atualiza o proprietário do automóvel conforme o tipo; `PROPRIEDADE_BANCO` exige agente bancário |
| GET | `/contratos/aluguel` | Agente ou cliente | Agente: todos os contratos. Cliente: somente os contratos dos próprios pedidos |
| GET | `/contratos/aluguel/{id}` | Agente | Consulta um contrato de aluguel |
| POST | `/contratos/credito` | Agente bancário | Registra crédito do banco autenticado |
| GET | `/contratos/credito` | Agente ou cliente | Agente: todos os créditos. Cliente: somente os créditos dos próprios contratos |
| GET | `/contratos/credito/{id}` | Agente | Consulta um contrato de crédito |

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

> O cadastro de agentes possui endpoints de provisionamento, mas não há formulário público no frontend. Em ambiente real, o provisionamento de perfis privilegiados deve ser restrito por administrador ou chave de configuração.

## Provisionamento de agentes (somente para ambiente de demonstração)
Os endpoints `POST /agentes/empresas` e `POST /agentes/bancos` recebem, respectivamente, os campos:
- Empresa: `nome`, `login`, `senha`, `cnpj`, `razaoSocial`.
- Banco: `nome`, `login`, `senha`, `cnpj`, `codigoBanco`.

A implementação atual deixa esses dois endpoints disponíveis para configurar as contas de demonstração. Isso é uma limitação de segurança do protótipo: antes de publicar, remova o acesso público e crie um fluxo administrativo controlado.
