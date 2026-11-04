# Histórias de Usuário

## Sistema de Aluguel de Carros

### 1. Cadastro e autenticação

**HU01 — Cadastro de usuário**\
Como usuário, quero realizar meu cadastro para ter acesso ao sistema.

**HU02 — Login**\
Como usuário cadastrado, quero realizar login com minhas credenciais
para acessar as funcionalidades do sistema.

### 2. Gestão de clientes

**HU03 — Cadastrar cliente**\
Como usuário autorizado, quero cadastrar os dados de um cliente para
registrar suas informações no sistema.

**HU04 — Consultar cliente**\
Como usuário autorizado, quero consultar os dados de um cliente para
visualizar suas informações.

**HU05 — Modificar cliente**\
Como usuário autorizado, quero modificar os dados de um cliente para
manter suas informações atualizadas.

**HU06 — Excluir cliente**\
Como usuário autorizado, quero excluir o cadastro de um cliente para
remover seu registro do sistema.

### 3. Pedidos de aluguel

**HU07 — Introduzir pedido**\
Como cliente, quero introduzir um pedido de aluguel de automóvel para
iniciar o processo de contratação.

**HU08 — Consultar pedido**\
Como cliente, quero consultar meus pedidos para acompanhar suas
informações e situação (status).

**HU09 — Modificar pedido**\
Como cliente, quero modificar um pedido de aluguel para atualizar suas
informações.

**HU10 — Cancelar pedido**\
Como cliente, quero cancelar um pedido de aluguel para desistir da
contratação.

### 4. Análise dos pedidos

**HU11 — Consultar pedidos**\
Como agente, quero consultar os pedidos de aluguel para conhecer os
pedidos recebidos.

**HU12 — Modificar pedido**\
Como agente, quero modificar um pedido para atualizar suas informações
durante a análise.

**HU13 — Avaliar pedido**\
Como agente, quero realizar a análise financeira de um pedido para
emitir um parecer sobre ele.

### 5. Automóveis e contratos

**HU14 — Registrar automóvel**\
Como usuário autorizado, quero registrar um automóvel informando
matrícula, ano, marca, modelo e placa para manter seus dados no sistema.

**HU15 — Registrar propriedade**\
Como usuário autorizado, quero associar um automóvel ao seu proprietário
para identificar se pertence a um cliente, empresa ou banco, conforme o
contrato.

**HU16 — Registrar contrato de crédito**\
Como agente do banco, quero registrar um contrato de crédito concedido
para relacioná-lo ao aluguel do automóvel.

---

**Observação:** O enunciado exige o CRUD de cliente, mas não especifica
completamente qual perfil de usuário será responsável por cada operação.
Por isso, as histórias HU03 a HU06 utilizam a expressão "usuário
autorizado", sem atribuir essa responsabilidade a um ator não definido.
