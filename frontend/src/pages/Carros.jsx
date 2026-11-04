import { useState, useEffect } from 'react'
import { api } from '../api'

export default function Carros({ usuario, onPedido }) {
  const [carros, setCarros] = useState([])
  const [escolhido, setEscolhido] = useState(null)
  const [inicio, setInicio] = useState('')
  const [fim, setFim] = useState('')
  const [erro, setErro] = useState('')

  useEffect(() => {
    api.listarCarros().then(setCarros).catch((e) => setErro(e.message))
  }, [])

  async function alugar(e) {
    e.preventDefault()
    setErro('')
    try {
      await api.criarPedido({
        clienteId: usuario.id,
        automovelId: escolhido.id,
        dataInicio: inicio,
        dataFim: fim,
      })
      onPedido() // vai para "Meus pedidos"
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <main className="pagina">
      <h2>Escolha seu carro</h2>
      {erro && <p className="erro">{erro}</p>}
      {carros.length === 0 && !erro && <p>Nenhum carro cadastrado ainda.</p>}

      <div className="cards">
        {carros.map((c) => (
          <div className="card" key={c.id}>
            <div className="foto">🚗</div>
            <h3>{c.marca} {c.modelo}</h3>
            <p className="detalhe">Ano {c.ano} · Placa {c.placa}</p>
            <button className="btn-amarelo" onClick={() => setEscolhido(c)}>Alugar agora</button>
          </div>
        ))}
      </div>

      {escolhido && (
        <div className="fundo" onClick={() => setEscolhido(null)}>
          <form className="caixa" onClick={(e) => e.stopPropagation()} onSubmit={alugar}>
            <h2>Alugar {escolhido.marca} {escolhido.modelo}</h2>
            <label>Data de retirada</label>
            <input type="date" value={inicio} onChange={(e) => setInicio(e.target.value)} required />
            <label>Data de devolução</label>
            <input type="date" value={fim} onChange={(e) => setFim(e.target.value)} required />
            {erro && <p className="erro">{erro}</p>}
            <button className="btn-amarelo" type="submit">Enviar pedido</button>
            <button type="button" className="link" onClick={() => setEscolhido(null)}>Cancelar</button>
          </form>
        </div>
      )}
    </main>
  )
}
