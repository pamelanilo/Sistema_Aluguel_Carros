import { useState, useEffect, useCallback } from 'react'
import { api } from '../api'
import { Alerta, Badge, Botao, Campo, Modal, Spinner, Vazio, brl, dataBr, useToast } from '../ui.jsx'

const FILTROS = [['TODOS', 'Todos'], ['PENDENTE', 'Pendentes'], ['EM_ANALISE', 'Em análise'], ['APROVADO', 'Aprovados'], ['REPROVADO', 'Recusados'], ['CANCELADO', 'Cancelados']]

function Trilha({ p }) {
  const rec = p.status === 'REPROVADO', canc = p.status === 'CANCELADO'
  const nivel = { PENDENTE: 1, EM_ANALISE: 2, APROVADO: 3, REPROVADO: 3, CANCELADO: 1 }[p.status]
  const passos = [['Solicitado', 1], ['Em análise', 2], [rec ? 'Recusado' : 'Aprovado', 3], ['Contrato', 4]]
  return <div className="trilha">{passos.map(([r, n]) => {
    const feito = n < nivel || (n === nivel && !canc) || (n === 4 && p.contratoAluguelId)
    const cls = n === 3 && rec && feito ? 'ruim' : feito ? 'ok' : ''
    return <div key={n} className={'passo ' + cls}>{r}</div>
  })}</div>
}

export default function Pedidos({ usuario }) {
  const cliente = usuario.tipo === 'Cliente'
  const [pedidos, setPedidos] = useState(null)
  const [filtro, setFiltro] = useState('TODOS')
  const [erro, setErro] = useState('')
  const [modal, setModal] = useState(null) // {tipo, pedido}
  const [ocupado, setOcupado] = useState(null)
  const toast = useToast()

  const carregar = useCallback(async () => {
    try { setPedidos(await api.listarPedidos()); setErro('') } catch (e) { setErro(e.message); setPedidos([]) }
  }, [])
  useEffect(() => { carregar() }, [carregar])

  async function executar(id, fn, msg) {
    setOcupado(id); setErro('')
    try { await fn(); toast(msg); await carregar() } catch (e) { setErro(e.message) } finally { setOcupado(null) }
  }
  const lista = (pedidos || []).filter((p) => filtro === 'TODOS' || p.status === filtro).sort((a, b) => b.id - a.id)
  const ativo = (p) => ['PENDENTE', 'EM_ANALISE'].includes(p.status)

  return <main className="pagina">
    <div className="cab"><div><h2>{cliente ? 'Meus pedidos' : 'Análise de pedidos'}</h2>
      <p>{cliente ? 'Acompanhe o andamento das suas solicitações.' : 'Inicie a análise financeira e aprove ou recuse os pedidos.'}</p></div></div>
    <div className="filtros">{FILTROS.map(([v, r]) => <button key={v} className={'chip' + (filtro === v ? ' ativo' : '')} onClick={() => setFiltro(v)}>{r}</button>)}</div>
    <Alerta onFechar={() => setErro('')}>{erro}</Alerta>
    {pedidos === null ? <Spinner /> : lista.length === 0 ? <Vazio titulo="Nenhum pedido encontrado" texto={cliente ? 'Escolha um carro no catálogo para solicitar um aluguel.' : 'Não há pedidos neste filtro.'} /> :
      <div className="lista">{lista.map((p) => <article className="cartao item" key={p.id}>
        <div>
          <h3>Pedido #{p.id} · {p.marca} {p.modelo} <Badge valor={p.status} /></h3>
          <div className="info">
            {!cliente && <span>Cliente: <b>{p.clienteNome}</b></span>}
            <span>Placa: <b>{p.placa}</b></span>
            <span>Retirada: <b>{dataBr(p.dataInicio)}</b></span>
            <span>Devolução: <b>{dataBr(p.dataFim)}</b></span>
            <span>Valor: <b>{brl(p.valorTotal)}</b></span>
            {p.contratoAluguelId && <span>Contrato: <b>#{p.contratoAluguelId}</b></span>}
          </div>
        </div>
        <div className="acoes">
          {ativo(p) && <button className="btn sec sm" onClick={() => setModal({ tipo: 'editar', p })}>Alterar</button>}
          {cliente && ativo(p) && <button className="btn perigo sm" onClick={() => setModal({ tipo: 'cancelar', p })}>Cancelar</button>}
          {!cliente && p.status === 'PENDENTE' && <Botao className="sm" variante="teal" carregando={ocupado === p.id} onClick={() => executar(p.id, () => api.analisarPedido(p.id), 'Análise financeira iniciada.')}>Iniciar análise</Botao>}
          {!cliente && p.status === 'EM_ANALISE' && <>
            <button className="btn teal sm" onClick={() => setModal({ tipo: 'aprovar', p })}>Aprovar</button>
            <button className="btn perigo sm" onClick={() => setModal({ tipo: 'recusar', p })}>Recusar</button></>}
          {!cliente && p.status === 'APROVADO' && !p.contratoAluguelId && <button className="btn primario sm" onClick={() => setModal({ tipo: 'contrato', p })}>Gerar contrato</button>}
        </div>
        {p.parecerFinanceiro && <div className="parecer"><b>{p.status === 'REPROVADO' ? 'Justificativa da recusa: ' : 'Parecer: '}</b>{p.parecerFinanceiro}</div>}
        <Trilha p={p} />
      </article>)}</div>}

    {modal?.tipo === 'editar' && <FormEditar p={modal.p} onFechar={() => setModal(null)} onOk={async () => { setModal(null); toast('Pedido atualizado.'); await carregar() }} />}
    {modal?.tipo === 'cancelar' && <Modal titulo="Cancelar pedido" onFechar={() => setModal(null)}>
      <p>Deseja cancelar o pedido <b>#{modal.p.id}</b> ({modal.p.marca} {modal.p.modelo})? Esta ação não pode ser desfeita.</p>
      <div className="rodape-form"><button className="btn fantasma" onClick={() => setModal(null)}>Manter pedido</button>
        <Botao variante="perigo" onClick={async () => { const p = modal.p; setModal(null); await executar(p.id, () => api.cancelarPedido(p.id), 'Pedido cancelado.') }}>Cancelar pedido</Botao></div>
    </Modal>}
    {(modal?.tipo === 'aprovar' || modal?.tipo === 'recusar') && <FormAvaliar modal={modal} onFechar={() => setModal(null)}
      onOk={async (msg) => { setModal(null); toast(msg); await carregar() }} />}
    {modal?.tipo === 'contrato' && <FormContrato p={modal.p} usuario={usuario} onFechar={() => setModal(null)}
      onOk={async () => { setModal(null); toast('Contrato de aluguel gerado e proprietário atualizado.'); await carregar() }} />}
  </main>
}

function FormEditar({ p, onFechar, onOk }) {
  const [f, setF] = useState({ dataInicio: p.dataInicio, dataFim: p.dataFim, valorTotal: p.valorTotal ?? '' })
  const [erro, setErro] = useState(''); const [env, setEnv] = useState(false)
  async function salvar(e) {
    e.preventDefault(); setErro('')
    if (f.dataFim <= f.dataInicio) return setErro('A devolução deve ser posterior à retirada.')
    setEnv(true)
    try { await api.atualizarPedido(p.id, { automovelId: p.automovelId, dataInicio: f.dataInicio, dataFim: f.dataFim, valorTotal: f.valorTotal === '' ? null : Number(f.valorTotal) }); await onOk() }
    catch (err) { setErro(err.message) } finally { setEnv(false) }
  }
  return <Modal titulo={`Alterar pedido #${p.id}`} onFechar={onFechar}><form className="empilha" onSubmit={salvar}>
    <Alerta>{erro}</Alerta>
    <Campo rotulo="Data de retirada"><input type="date" required value={f.dataInicio} onChange={(e) => setF({ ...f, dataInicio: e.target.value })} /></Campo>
    <Campo rotulo="Data de devolução"><input type="date" required value={f.dataFim} onChange={(e) => setF({ ...f, dataFim: e.target.value })} /></Campo>
    <Campo rotulo="Valor total (R$)"><input type="number" min="0" step="0.01" value={f.valorTotal} onChange={(e) => setF({ ...f, valorTotal: e.target.value })} /></Campo>
    <div className="rodape-form"><button type="button" className="btn fantasma" onClick={onFechar}>Voltar</button><Botao carregando={env}>Salvar</Botao></div>
  </form></Modal>
}

function FormAvaliar({ modal, onFechar, onOk }) {
  const aprovar = modal.tipo === 'aprovar'
  const [parecer, setParecer] = useState(''); const [erro, setErro] = useState(''); const [env, setEnv] = useState(false)
  async function enviar(e) {
    e.preventDefault()
    if (!parecer.trim()) return setErro(aprovar ? 'Informe o parecer financeiro.' : 'A recusa exige uma justificativa.')
    setEnv(true)
    try { await api.avaliarPedido(modal.p.id, aprovar, parecer.trim()); await onOk(aprovar ? 'Pedido aprovado.' : 'Pedido recusado.') }
    catch (err) { setErro(err.message); setEnv(false) }
  }
  return <Modal titulo={`${aprovar ? 'Aprovar' : 'Recusar'} pedido #${modal.p.id}`} onFechar={onFechar}><form className="empilha" onSubmit={enviar}>
    <div className="resumo"><b>{modal.p.clienteNome}</b>{modal.p.marca} {modal.p.modelo} · {dataBr(modal.p.dataInicio)} a {dataBr(modal.p.dataFim)}</div>
    <Alerta>{erro}</Alerta>
    <Campo rotulo={aprovar ? 'Parecer financeiro' : 'Justificativa da recusa (obrigatória)'}><textarea value={parecer} onChange={(e) => setParecer(e.target.value)} autoFocus /></Campo>
    <div className="rodape-form"><button type="button" className="btn fantasma" onClick={onFechar}>Voltar</button>
      <Botao variante={aprovar ? 'teal' : 'perigo'} carregando={env}>{aprovar ? 'Confirmar aprovação' : 'Confirmar recusa'}</Botao></div>
  </form></Modal>
}

function FormContrato({ p, usuario, onFechar, onOk }) {
  const banco = usuario.tipo === 'AgenteBanco'
  const [tipo, setTipo] = useState('PROPRIEDADE_EMPRESA')
  const [cred, setCred] = useState({ valor: '', taxa: '', parcelas: '' })
  const [erro, setErro] = useState(''); const [env, setEnv] = useState(false)
  async function gerar(e) {
    e.preventDefault(); setErro(''); setEnv(true)
    try {
      const r = await api.criarContratoAluguel(p.id, tipo)
      if (banco && cred.valor) {
        await api.criarContratoCredito({ contratoAluguelId: r.id, agenteBancoId: usuario.id, valorConcedido: Number(cred.valor), taxaJuros: Number(cred.taxa), parcelas: Number(cred.parcelas) })
      }
      await onOk()
    } catch (err) { setErro(err.message); setEnv(false) }
  }
  return <Modal titulo={`Gerar contrato · pedido #${p.id}`} onFechar={onFechar}><form className="empilha" onSubmit={gerar}>
    <Alerta>{erro}</Alerta>
    <Campo rotulo="Tipo de contrato (define o novo proprietário)" dica="Ao gerar, o proprietário do automóvel é atualizado e o contrato fica como executado.">
      <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
        <option value="PROPRIEDADE_CLIENTE">Propriedade do cliente</option><option value="PROPRIEDADE_EMPRESA">Propriedade da empresa</option>
        {banco && <option value="PROPRIEDADE_BANCO">Propriedade do banco</option>}
      </select></Campo>
    {banco ? <>
      <b>Contrato de crédito (opcional)</b>
      <div className="form-grade">
        <Campo rotulo="Valor concedido (R$)"><input type="number" min="0.01" step="0.01" value={cred.valor} onChange={(e) => setCred({ ...cred, valor: e.target.value })} /></Campo>
        <Campo rotulo="Taxa de juros (%)"><input type="number" min="0" step="0.01" required={!!cred.valor} value={cred.taxa} onChange={(e) => setCred({ ...cred, taxa: e.target.value })} /></Campo>
        <Campo rotulo="Parcelas"><input type="number" min="1" required={!!cred.valor} value={cred.parcelas} onChange={(e) => setCred({ ...cred, parcelas: e.target.value })} /></Campo>
      </div></> : <Alerta tipo="info">Somente um agente bancário pode conceder contratos de crédito.</Alerta>}
    <div className="rodape-form"><button type="button" className="btn fantasma" onClick={onFechar}>Voltar</button><Botao carregando={env}>Gerar contrato</Botao></div>
  </form></Modal>
}
