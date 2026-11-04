import { useState, useEffect } from 'react'
import { api } from '../api'
import { Alerta, Botao, Campo, Modal, Spinner, Vazio, CarroArte, useToast } from '../ui.jsx'

export default function Catalogo({ onPedido }) {
  const [carros, setCarros] = useState(null)
  const [busca, setBusca] = useState('')
  const [erro, setErro] = useState('')
  const [alvo, setAlvo] = useState(null)
  const [f, setF] = useState({ dataInicio: '', dataFim: '', valorTotal: '' })
  const [erroModal, setErroModal] = useState('')
  const [enviando, setEnviando] = useState(false)
  const toast = useToast()
  const hoje = new Date().toISOString().slice(0, 10)

  useEffect(() => { api.listarCarros().then(setCarros).catch((e) => { setErro(e.message); setCarros([]) }) }, [])

  const lista = (carros || []).filter((c) => `${c.marca} ${c.modelo} ${c.ano} ${c.placa}`.toLowerCase().includes(busca.toLowerCase()))

  async function enviar(e) {
    e.preventDefault(); setErroModal('')
    if (f.dataFim <= f.dataInicio) return setErroModal('A devolução deve ser posterior à retirada.')
    setEnviando(true)
    try {
      await api.criarPedido({ automovelId: alvo.id, dataInicio: f.dataInicio, dataFim: f.dataFim, valorTotal: f.valorTotal ? Number(f.valorTotal) : null })
      toast('Pedido enviado! Acompanhe o andamento em “Meus pedidos”.')
      setAlvo(null); onPedido()
    } catch (err) { setErroModal(err.message) } finally { setEnviando(false) }
  }

  return <main className="pagina">
    <div className="cab"><div><h2>Catálogo de automóveis</h2><p>Escolha um veículo e solicite seu aluguel.</p></div>
      <input className="busca" placeholder="Buscar marca, modelo ou placa…" value={busca} onChange={(e) => setBusca(e.target.value)} /></div>
    <Alerta>{erro}</Alerta>
    {carros === null ? <Spinner /> : lista.length === 0 ? <Vazio icone="🚘" titulo="Nenhum automóvel encontrado" texto="Tente outra busca ou volte mais tarde." /> :
      <div className="grade-cards">{lista.map((c) => <article className="cartao carro" key={c.id}>
        <div className="carro-img"><CarroArte seed={c.modelo + c.placa} /><em>{c.ano}</em></div>
        <div className="carro-corpo"><h3>{c.marca} {c.modelo}</h3><span className="placa">{c.placa}</span>
          <span className="meta">Matrícula {c.matricula}</span>
          <button className="btn primario" onClick={() => { setAlvo(c); setF({ dataInicio: '', dataFim: '', valorTotal: '' }); setErroModal('') }}>Solicitar aluguel</button></div>
      </article>)}</div>}

    {alvo && <Modal titulo="Solicitar aluguel" onFechar={() => setAlvo(null)}>
      <form className="empilha" onSubmit={enviar}>
        <div className="resumo"><b>{alvo.marca} {alvo.modelo} · {alvo.ano}</b>Placa {alvo.placa}</div>
        <Alerta>{erroModal}</Alerta>
        <Campo rotulo="Data de retirada"><input type="date" min={hoje} required value={f.dataInicio} onChange={(e) => setF({ ...f, dataInicio: e.target.value })} /></Campo>
        <Campo rotulo="Data de devolução"><input type="date" min={f.dataInicio || hoje} required value={f.dataFim} onChange={(e) => setF({ ...f, dataFim: e.target.value })} /></Campo>
        <Campo rotulo="Valor total estimado (R$) — opcional"><input type="number" min="0" step="0.01" value={f.valorTotal} onChange={(e) => setF({ ...f, valorTotal: e.target.value })} /></Campo>
        <div className="rodape-form"><button type="button" className="btn fantasma" onClick={() => setAlvo(null)}>Voltar</button><Botao carregando={enviando}>Enviar pedido</Botao></div>
      </form>
    </Modal>}
  </main>
}
