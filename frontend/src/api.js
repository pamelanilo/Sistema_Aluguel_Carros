// Único lugar que conversa com o backend. Se algum endereço do seu backend for diferente, ajuste AQUI.
async function chamar(caminho, metodo = 'GET', corpo) {
  const resposta = await fetch('/api' + caminho, {
    method: metodo,
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include', // envia o cookie da sessão (é ele que mantém o login)
    body: corpo ? JSON.stringify(corpo) : undefined,
  })
  const texto = await resposta.text()
  let dados = null
  try { dados = texto ? JSON.parse(texto) : null } catch { dados = texto }
  if (!resposta.ok) {
    throw new Error((dados && dados.mensagem) || (typeof dados === 'string' && dados) || 'Erro ' + resposta.status)
  }
  return dados
}

export const api = {
  login: (login, senha) => chamar('/auth/login', 'POST', { login, senha }),
  logout: () => chamar('/auth/logout', 'POST'),
  eu: () => chamar('/auth/me'),
  cadastrarCliente: (cliente) => chamar('/clientes', 'POST', cliente),
  listarCarros: () => chamar('/automoveis'),
  listarPedidos: () => chamar('/pedidos'),
  criarPedido: (pedido) => chamar('/pedidos', 'POST', pedido),
  cancelarPedido: (id) => chamar('/pedidos/' + id + '/cancelar', 'PATCH'),
  avaliarPedido: (id, aprovado) => chamar('/pedidos/' + id + '/avaliar', 'PATCH', {
    status: aprovado ? 'APROVADO' : 'REPROVADO',
    parecer: aprovado ? 'Pedido aprovado pelo agente.' : 'Pedido reprovado pelo agente.'
  }),
}
