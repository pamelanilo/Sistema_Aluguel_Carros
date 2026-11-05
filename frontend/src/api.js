const API_BASE = import.meta.env.VITE_API_URL
  ? import.meta.env.VITE_API_URL.replace(/\/$/, '')
  : '/api'

async function chamar(caminho, metodo = 'GET', corpo) {
  const resposta = await fetch(API_BASE + caminho, {
    method: metodo,
    headers: corpo ? { 'Content-Type': 'application/json' } : {},
    credentials: 'include',
    body: corpo ? JSON.stringify(corpo) : undefined,
  })
  const texto = await resposta.text()
  let dados = null
  try { dados = texto ? JSON.parse(texto) : null } catch { dados = texto }
  if (!resposta.ok) {
    const erros = dados?.erros ? Object.values(dados.erros).join(' · ') : null
    const mensagem = erros || dados?.mensagem || dados?.detail || (typeof dados === 'string' ? dados : null)
    const e = new Error(mensagem || `Erro ${resposta.status}`)
    e.status = resposta.status
    throw e
  }
  return dados
}

export const api = {
  login: (login, senha) => chamar('/auth/login', 'POST', { login, senha }),
  logout: () => chamar('/auth/logout', 'POST'),
  eu: () => chamar('/auth/me'),
  meusDados: () => chamar('/clientes/me'),
  atualizarMeusDados: (c) => chamar('/clientes/me', 'PUT', c),
  cadastrarCliente: (c) => chamar('/clientes', 'POST', c),
  cadastrarEmpresa: (a) => chamar('/agentes/empresas', 'POST', a),
  cadastrarBanco: (a) => chamar('/agentes/bancos', 'POST', a),
  cadastroAgentesPublico: () => chamar('/agentes/cadastro-publico'),
  listarClientes: () => chamar('/clientes'),
  atualizarCliente: (id, c) => chamar(`/clientes/${id}`, 'PUT', c),
  excluirCliente: (id) => chamar(`/clientes/${id}`, 'DELETE'),
  listarEmpresas: () => chamar('/agentes/empresas'),
  listarBancos: () => chamar('/agentes/bancos'),
  listarCarros: () => chamar('/automoveis'),
  cadastrarCarro: (c) => chamar('/automoveis', 'POST', c),
  atualizarCarro: (id, c) => chamar(`/automoveis/${id}`, 'PUT', c),
  excluirCarro: (id) => chamar(`/automoveis/${id}`, 'DELETE'),
  listarPedidos: () => chamar('/pedidos'),
  criarPedido: (p) => chamar('/pedidos', 'POST', p),
  atualizarPedido: (id, p) => chamar(`/pedidos/${id}`, 'PUT', p),
  cancelarPedido: (id) => chamar(`/pedidos/${id}/cancelar`, 'PATCH'),
  analisarPedido: (id) => chamar(`/pedidos/${id}/analisar`, 'PATCH'),
  avaliarPedido: (id, aprovado, parecer) =>
    chamar(`/pedidos/${id}/avaliar`, 'PATCH', { status: aprovado ? 'APROVADO' : 'REPROVADO', parecer }),
  listarContratosAluguel: () => chamar('/contratos/aluguel'),
  listarContratosCredito: () => chamar('/contratos/credito'),
  criarContratoAluguel: (pedidoId, tipo) => chamar('/contratos/aluguel', 'POST', { pedidoId, tipo }),
  criarContratoCredito: (d) => chamar('/contratos/credito', 'POST', d),
}
