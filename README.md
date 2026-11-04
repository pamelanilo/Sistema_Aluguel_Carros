# Sistema de Aluguel de Carros

Sistema web para apoio à gestão de aluguéis de automóveis, que permite
efetuar, cancelar e modificar pedidos de aluguel pela Internet.

> Laboratório 02 — DIAW (Desenvolvimento e Integração de Aplicações Web)
> Engenharia de Software — PUC Minas

## Integrantes

- [Seu nome]

## Professor

- João Paulo Carneiro Aramuni

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java (JDK 25) |
| Back-end | Spring Boot 4.0.x (arquitetura MVC) |
| Build e dependências | Apache Maven |
| Front-end | React + Vite |
| Banco de dados | Supabase (PostgreSQL) |
| Autenticação | Login com usuário e senha |

## Modelagem

Os diagramas ficam em [`docs/diagramas`](docs/diagramas), em PNG e no
código-fonte PlantUML (`.puml`):

- Diagrama de Casos de Uso
- Diagrama de Classes
- Diagrama de Pacotes (Visão Lógica)

As histórias de usuário estão em
[`docs/historias-de-usuario.md`](docs/historias-de-usuario.md).

## Arquitetura

O back-end segue o padrão MVC, organizado nos pacotes (dentro de
`br.com.aluguelcarros.backend`):

| Pacote | Responsabilidade |
|---|---|
| `controller` | Recebe as requisições HTTP e devolve as respostas |
| `service` | Regras de negócio |
| `repository` | Acesso ao banco de dados (Spring Data JPA) |
| `model` | Entidades (e `model.enums` para os enums) |
| `dto` | Objetos de transferência de dados |
| `config` | Configurações (CORS, segurança) |

O front-end em React + Vite consome a API REST do back-end.

## Funcionalidades

- [ ] Cadastro de usuários
- [ ] Login (clientes e agentes)
- [ ] CRUD de cliente
- [ ] Criar pedido de aluguel
- [ ] Consultar status do pedido
- [ ] Cancelar e modificar pedido
- [ ] Avaliação do pedido pelo agente

## Estrutura de pastas

```
Sistema_Aluguel_Carros/
├── backend/      # Spring Boot (Java 25, Maven)
├── frontend/     # React + Vite
├── docs/
│   ├── diagramas/
│   └── historias-de-usuario.md
└── README.md
```

## Como executar

### Pré-requisitos

- JDK 25
- Node.js (versão LTS)
- Projeto criado no Supabase

### Back-end

1. Defina a variável de ambiente com a senha do banco:

   ```bash
   # Linux / macOS
   export SUPABASE_DB_PASSWORD="sua-senha"

   # Windows (PowerShell)
   $env:SUPABASE_DB_PASSWORD="sua-senha"
   ```

2. Execute:

   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

A API fica disponível em `http://localhost:8080`.

### Front-end

```bash
cd frontend
npm install
npm run dev
```

A aplicação fica disponível em `http://localhost:5173`.

## Regras de negócio

- O sistema só pode ser usado após cadastro e login.
- Clientes introduzem, modificam, consultam e cancelam pedidos de aluguel.
- Agentes (empresas e bancos) modificam e avaliam pedidos.
- Cada cliente pode ter no máximo 3 rendimentos (entidade empregadora e valor).
- O automóvel pode pertencer a um cliente, empresa ou banco.
- O aluguel pode estar associado a um contrato de crédito concedido por um banco.
