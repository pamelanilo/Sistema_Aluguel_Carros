import { useState, useEffect } from 'react'
import { api } from './api'
import Login from './pages/Login.jsx'
import Cadastro from './pages/Cadastro.jsx'
import Carros from './pages/Carros.jsx'
import Pedidos from './pages/Pedidos.jsx'

export default function App() {
  const [usuario, setUsuario] = useState(null) // quem está logado
  const [tela, setTela] = useState('login')    // qual tela mostrar
  const [carregando, setCarregando] = useState(true)

  // Ao abrir o site, pergunta ao backend se já existe login ativo
  useEffect(() => {
    api.eu()
      .then((u) => { setUsuario(u); setTela(u.tipo === 'Cliente' ? 'carros' : 'pedidos') })
      .catch(() => {})
      .finally(() => setCarregando(false))
  }, [])

  function entrou(u) {
    setUsuario(u)
    setTela(u.tipo === 'Cliente' ? 'carros' : 'pedidos')
  }

  async function sair() {
    try { await api.logout() } catch {}
    setUsuario(null)
    setTela('login')
  }

  if (carregando) return <p className="centro">Carregando...</p>

  const ehCliente = usuario && usuario.tipo === 'Cliente'

  return (
    <>
      <header className="topo">
        <div className="logo">🚗 Vrum<span>!</span></div>
        {usuario && (
          <nav>
            {ehCliente && <button className="link" onClick={() => setTela('carros')}>Carros</button>}
            <button className="link" onClick={() => setTela('pedidos')}>
              {ehCliente ? 'Meus pedidos' : 'Painel do agente'}
            </button>
            <span className="ola">Olá, {usuario.nome}</span>
            <button className="btn-escuro" onClick={sair}>Sair</button>
          </nav>
        )}
      </header>

      {!usuario && tela === 'login' && <Login onEntrar={entrou} onCadastrar={() => setTela('cadastro')} />}
      {!usuario && tela === 'cadastro' && <Cadastro onVoltar={() => setTela('login')} />}
      {usuario && tela === 'carros' && <Carros usuario={usuario} onPedido={() => setTela('pedidos')} />}
      {usuario && tela === 'pedidos' && <Pedidos usuario={usuario} />}

      <footer className="rodape">Vrum! © 2026 — Sistema de Aluguel de Carros · DIAW</footer>
    </>
  )
}
