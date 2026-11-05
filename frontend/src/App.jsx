import { useState, useEffect } from 'react'
import { api } from './api'
import { Spinner, Logo } from './ui.jsx'
import Login from './pages/Login.jsx'
import Cadastro from './pages/Cadastro.jsx'
import Inicio from './pages/Inicio.jsx'
import Catalogo from './pages/Catalogo.jsx'
import Pedidos from './pages/Pedidos.jsx'
import Contratos from './pages/Contratos.jsx'
import GestaoCarros from './pages/GestaoCarros.jsx'
import GestaoClientes from './pages/GestaoClientes.jsx'
import MeusDados from './pages/MeusDados.jsx'
import DesignSystem from './pages/DesignSystem.jsx'

const PERFIL = { Cliente: 'Cliente', AgenteEmpresa: 'Agente (empresa)', AgenteBanco: 'Agente (banco)' }
const MENU_CLIENTE = [['inicio', 'Início'], ['catalogo', 'Catálogo'], ['pedidos', 'Meus pedidos'], ['contratos', 'Contratos'], ['meus-dados', 'Meus dados'], ['design-system', 'Design System']]
const MENU_AGENTE = [['inicio', 'Início'], ['pedidos', 'Análise de pedidos'], ['contratos', 'Contratos'], ['gestao-carros', 'Automóveis'], ['gestao-clientes', 'Clientes'], ['design-system', 'Design System']]
const MENU_PUBLICO = [['login', 'Entrar'], ['cadastro', 'Criar conta'], ['design-system', 'Design System']]
const hashAtual = () => window.location.hash.slice(1)

// Tema: 'light' | 'dark' | null (null = segue o sistema operacional). Guardado só neste navegador.
function lerTema() { try { return localStorage.getItem('vrum-tema') } catch { return null } }
function temaEfetivo(t) { return t || (window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light') }

export default function App() {
  const [usuario, setUsuario] = useState(null)
  const [tela, setTela] = useState(hashAtual())
  const [carregando, setCarregando] = useState(true)
  const [tema, setTema] = useState(() => temaEfetivo(lerTema()))

  useEffect(() => { document.documentElement.setAttribute('data-theme', tema) }, [tema])
  function alternarTema() {
    const novo = tema === 'dark' ? 'light' : 'dark'
    setTema(novo)
    try { localStorage.setItem('vrum-tema', novo) } catch { /* armazenamento indisponível */ }
  }

  // A tela fica no hash da URL: o F5 e o botão "voltar" do navegador mantêm o lugar do usuário.
  useEffect(() => {
    const f = () => setTela(hashAtual())
    window.addEventListener('hashchange', f)
    return () => window.removeEventListener('hashchange', f)
  }, [])
  const ir = (t) => { if (hashAtual() === t) setTela(t); else window.location.hash = t }

  useEffect(() => {
    api.eu().then(setUsuario).catch(() => {}).finally(() => setCarregando(false))
  }, [])

  async function sair() {
    try { await api.logout() } catch { /* sessão já encerrada */ }
    setUsuario(null); ir('login')
  }

  if (carregando) return <Spinner texto="Iniciando VRUM!…" />
  const cliente = usuario?.tipo === 'Cliente'
  const menu = !usuario ? MENU_PUBLICO : cliente ? MENU_CLIENTE : MENU_AGENTE
  const permitidas = menu.map((m) => m[0])
  const atual = permitidas.includes(tela) ? tela : permitidas[0]

  return <>
    <header className="topo"><div className="topo-in">
      <Logo />
      <nav className="nav" aria-label="Principal">
        {menu.map(([id, rotulo]) => <button key={id} className={atual === id ? 'ativo' : ''} aria-current={atual === id ? 'page' : undefined} onClick={() => ir(id)}>{rotulo}</button>)}
      </nav>
      <div className="topo-acoes">
        <button className="tema" onClick={alternarTema} aria-label={tema === 'dark' ? 'Ativar modo claro' : 'Ativar modo escuro'}
          title={tema === 'dark' ? 'Modo claro' : 'Modo escuro'}>{tema === 'dark' ? '☀️' : '🌙'}</button>
        {usuario && <div className="user">
          <div><b>{usuario.nome}</b><span className="perfil">{PERFIL[usuario.tipo] || usuario.tipo}</span></div>
          <button className="btn sec sm" onClick={sair}>Sair</button>
        </div>}
      </div>
    </div></header>

    {!usuario && atual === 'login' && <Login onEntrar={(u) => { setUsuario(u); ir('inicio') }} onCadastrar={() => ir('cadastro')} />}
    {!usuario && atual === 'cadastro' && <Cadastro onVoltar={() => ir('login')} />}
    {usuario && atual === 'inicio' && <Inicio usuario={usuario} ir={ir} />}
    {usuario && cliente && atual === 'catalogo' && <Catalogo onPedido={() => ir('pedidos')} />}
    {usuario && atual === 'pedidos' && <Pedidos usuario={usuario} />}
    {usuario && atual === 'contratos' && <Contratos usuario={usuario} />}
    {atual === 'design-system' && <DesignSystem />}
    {usuario && cliente && atual === 'meus-dados' && <MeusDados onAtualizado={() => api.eu().then(setUsuario).catch(() => {})} />}
    {usuario && !cliente && atual === 'gestao-carros' && <GestaoCarros />}
    {usuario && !cliente && atual === 'gestao-clientes' && <GestaoClientes />}
    <footer className="rodape">VRUM! © 2026 · Sistema de Aluguel de Automóveis · Engenharia de Software · PUC Minas ·
      <button className="link" onClick={() => ir('design-system')}>Design System</button></footer>
  </>
}
