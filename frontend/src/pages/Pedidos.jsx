import { useState, useEffect } from 'react'
import { api } from '../api'

export default function Pedidos({ usuario }) {
  const [pedidos, setPedidos] = useState([])
  const [erro, setErro] = useState('')
  const ehCliente = usuario.tipo === 'CLIENTE'

  function carregar() {
    api.listarPedidos().then(setPedidos).catch((e) => setErro(e.message))
  }
  useEffect(carregar, [])

  async function cancelar(id) {
    try { await api.cancelarPedido(id); carregar() } catch (e) { setErro(e.message) }
  }
  async function avaliar(id, aprovado) {
    try { await api.avaliarPedido(id, aprovado); carregar() } catch (e) { setErro(e.message) }
  }

  return (
    <main className="pagina">
      <h2>{ehCliente ? 'Meus pedidos' : 'Pedidos para análise'}</h2>
      {erro && <p className="erro">{erro}</p>}
      {pedidos.length === 0 && <p>Nenhum pedido por aqui.</p>}

      <div className="lista">
        {pedidos.map((p) => (
          <div className="pedido" key={p.id}>
            <div>
              <strong>Pedido #{p.id} — {p.automovel?.marca} {p.automovel?.modelo}</strong>
              <p className="detalhe">
                {p.dataInicio} até {p.dataFim}
                {!ehCliente && p.cliente ? ' · Cliente: ' + p.cliente.nome : ''}
              </p>
            </div>
            <span className={'status ' + String(p.status).toLowerCase()}>{p.status}</span>
            <div className="acoes">
              {ehCliente && <button className="btn-escuro" onClick={() => cancelar(p.id)}>Cancelar</button>}
              {!ehCliente && (
                <>
                  <button className="btn-amarelo" onClick={() => avaliar(p.id, true)}>Aprovar</button>
                  <button className="btn-escuro" onClick={() => avaliar(p.id, false)}>Reprovar</button>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    </main>
  )
}
