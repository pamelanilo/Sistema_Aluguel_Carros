import { useState, useEffect, useCallback } from 'react'
import { api } from '../api'
import { Alerta, Badge, Botao, Campo, Modal, Spinner, Vazio, ROTULO, brl, dataBr, useToast } from '../ui.jsx'

export default function Contratos({ usuario }) {
  const banco = usuario.tipo === 'AgenteBanco'
  const cliente = usuario.tipo === 'Cliente'
  const [alugueis, setAlugueis] = useState(null)
  const [creditos, setCreditos] = useState([])
  const [erro, setErro] = useState('')
  const [alvo, setAlvo] = useState(null)
  const [f, setF] = useState({ valor: '', taxa: '', parcelas: '' })
  const [env, setEnv] = useState(false)
  const toast = useToast()

  const carregar = useCallback(async () => {
    try { const [a, c] = await Promise.all([api.listarContratosAluguel(), api.listarContratosCredito()]); setAlugueis(a); setCreditos(c); setErro('') }
    catch (e) { setErro(e.message); setAlugueis([]) }
  }, [])
  useEffect(() => { carregar() }, [carregar])

  async function conceder(e) {
    e.preventDefault(); setEnv(true); setErro('')
    try {
      await api.criarContratoCredito({ contratoAluguelId: alvo.id, agenteBancoId: usuario.id, valorConcedido: Number(f.valor), taxaJuros: Number(f.taxa), parcelas: Number(f.parcelas) })
      toast('Contrato de crédito concedido.'); setAlvo(null); await carregar()
    } catch (err) { setErro(err.message); setAlvo(null) } finally { setEnv(false) }
  }
  const credDe = (id) => creditos.find((c) => c.contratoAluguelId === id)

  return <main className="pagina">
    <div className="cab"><div><h2>Contratos</h2><p>{cliente ? 'Contratos de aluguel dos seus pedidos aprovados.' : 'Contratos de aluguel gerados a partir de pedidos aprovados e seus créditos.'}</p></div></div>
    <Alerta onFechar={() => setErro('')}>{erro}</Alerta>
    {alugueis === null ? <Spinner /> : alugueis.length === 0 ? <Vazio icone="📄" titulo="Nenhum contrato ainda" texto={cliente ? 'Quando um pedido seu for aprovado e o contrato gerado, ele aparece aqui.' : 'Aprove um pedido e gere o contrato na tela de análise.'} /> :
      <div className="cartao tabela-wrap"><table>
        <thead><tr><th>Contrato</th><th>Pedido</th>{!cliente && <th>Cliente</th>}<th>Automóvel</th><th>Tipo</th><th>Proprietário atual</th><th>Assinatura</th><th>Estado</th><th>Crédito</th></tr></thead>
        <tbody>{alugueis.map((c) => { const cr = credDe(c.id); return <tr key={c.id}>
          <td><b>#{c.id}</b></td><td>#{c.pedidoId}</td>{!cliente && <td>{c.cliente}</td>}<td>{c.automovel}</td><td>{ROTULO[c.tipo] || c.tipo}</td>
          <td>{c.proprietario || '—'}</td><td>{dataBr(c.dataAssinatura)}</td><td><Badge valor={c.status} /></td>
          <td>{cr ? <span>{brl(cr.valorConcedido)}<br /><small>{cr.parcelas}x · {cr.taxaJuros}%</small></span>
            : banco ? <button className="btn teal sm" onClick={() => { setAlvo(c); setF({ valor: '', taxa: '', parcelas: '' }) }}>Conceder crédito</button> : <small>Sem crédito</small>}</td>
        </tr> })}</tbody></table></div>}
    {alvo && <Modal titulo={`Crédito · contrato #${alvo.id}`} onFechar={() => setAlvo(null)}><form className="empilha" onSubmit={conceder}>
      <div className="resumo"><b>{alvo.cliente}</b>{alvo.automovel}</div>
      <Campo rotulo="Valor concedido (R$)"><input type="number" min="0.01" step="0.01" required value={f.valor} onChange={(e) => setF({ ...f, valor: e.target.value })} /></Campo>
      <Campo rotulo="Taxa de juros (%)"><input type="number" min="0" step="0.01" required value={f.taxa} onChange={(e) => setF({ ...f, taxa: e.target.value })} /></Campo>
      <Campo rotulo="Parcelas"><input type="number" min="1" required value={f.parcelas} onChange={(e) => setF({ ...f, parcelas: e.target.value })} /></Campo>
      <div className="rodape-form"><button type="button" className="btn fantasma" onClick={() => setAlvo(null)}>Voltar</button><Botao carregando={env}>Conceder crédito</Botao></div>
    </form></Modal>}
  </main>
}
