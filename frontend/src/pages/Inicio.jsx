import { useState, useEffect } from 'react'
import { api } from '../api'
import { Alerta, Badge, Spinner, dataBr } from '../ui.jsx'

function Indicador({ valor, rotulo, destaque, onClick }) {
  return <button type="button" className={'stat' + (destaque ? ' destaque' : '')} onClick={onClick}>
    <strong>{valor}</strong><span>{rotulo}</span>
  </button>
}

export default function Inicio({ usuario, ir }) {
  const cliente = usuario.tipo === 'Cliente'
  const [d, setD] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => {
    const busca = [api.listarPedidos(), api.listarContratosAluguel(), cliente ? Promise.resolve(null) : api.listarCarros()]
    Promise.all(busca)
      .then(([pedidos, contratos, carros]) => setD({ pedidos, contratos, carros }))
      .catch((e) => { setErro(e.message); setD({ pedidos: [], contratos: [], carros: [] }) })
  }, [cliente])

  if (!d) return <Spinner />
  const n = (st) => d.pedidos.filter((p) => st.includes(p.status)).length
  const recentes = [...d.pedidos]
    .filter((p) => (cliente ? true : ['PENDENTE', 'EM_ANALISE'].includes(p.status)))
    .sort((a, b) => b.id - a.id).slice(0, 5)
  const primeiroNome = (usuario.nome || '').split(' ')[0]

  return <main className="pagina">
    <div className="cab"><div>
      <h2>Olá, {primeiroNome}</h2>
      <p>{cliente ? 'Acompanhe seus pedidos ou escolha um novo automóvel.' : 'Veja o que precisa da sua atenção hoje.'}</p>
    </div>
      <div className="acoes">
        {cliente
          ? <button className="btn primario" onClick={() => ir('catalogo')}>Ver catálogo</button>
          : <button className="btn primario" onClick={() => ir('pedidos')}>Analisar pedidos</button>}
      </div></div>
    <Alerta onFechar={() => setErro('')}>{erro}</Alerta>

    <div className="stats">
      {cliente ? <>
        <Indicador valor={d.pedidos.length} rotulo="Pedidos feitos" onClick={() => ir('pedidos')} />
        <Indicador valor={n(['PENDENTE', 'EM_ANALISE'])} rotulo="Em andamento" destaque onClick={() => ir('pedidos')} />
        <Indicador valor={n(['APROVADO'])} rotulo="Aprovados" onClick={() => ir('pedidos')} />
        <Indicador valor={d.contratos.length} rotulo="Contratos" onClick={() => ir('contratos')} />
      </> : <>
        <Indicador valor={n(['PENDENTE'])} rotulo="Aguardando análise" destaque onClick={() => ir('pedidos')} />
        <Indicador valor={n(['EM_ANALISE'])} rotulo="Em análise" onClick={() => ir('pedidos')} />
        <Indicador valor={d.contratos.length} rotulo="Contratos" onClick={() => ir('contratos')} />
        <Indicador valor={d.carros.length} rotulo="Automóveis na frota" onClick={() => ir('gestao-carros')} />
      </>}
    </div>

    <section className="cartao bloco-lista">
      <h3>{cliente ? 'Seus pedidos mais recentes' : 'Pedidos que aguardam uma decisão'}</h3>
      {recentes.length === 0
        ? <p className="meta">{cliente ? 'Você ainda não fez nenhum pedido. Escolha um automóvel no catálogo para começar.' : 'Nenhum pedido pendente ou em análise no momento.'}</p>
        : <ul className="mini-lista">{recentes.map((p) => <li key={p.id}>
          <div><b>#{p.id} · {p.marca} {p.modelo}</b>
            <span className="meta">{!cliente && <>{p.clienteNome} · </>}{dataBr(p.dataInicio)} a {dataBr(p.dataFim)}</span></div>
          <Badge valor={p.status} />
        </li>)}</ul>}
    </section>
  </main>
}
