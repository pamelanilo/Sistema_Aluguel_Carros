import { useState } from 'react'
import { api } from '../api'
import { Alerta, Botao, Campo, CampoSenha, CarroArte } from '../ui.jsx'

export default function Login({ onEntrar, onCadastrar }) {
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  async function entrar(e) {
    e.preventDefault(); setErro(''); setEnviando(true)
    try { await api.login(login.trim(), senha); onEntrar(await api.eu()) }
    catch (err) { setErro(err.message) } finally { setEnviando(false) }
  }

  return <main className="auth">
    <section className="auth-hero">
      <h1>Alugue o carro certo,<br /><span>sem complicação.</span></h1>
      <p>Plataforma para solicitar, acompanhar e formalizar o aluguel de automóveis com análise financeira transparente.</p>
      <ul><li>Catálogo de automóveis atualizado</li><li>Acompanhamento do pedido em tempo real</li><li>Contratos de aluguel e crédito integrados</li></ul>
      <div className="hero-carro"><CarroArte seed="vrum" /></div>
    </section>
    <section className="auth-form">
      <form className="cartao auth-card" onSubmit={entrar}>
        <h2>Entrar</h2><p>Acesse com seu usuário e senha.</p>
        <div className="empilha">
          <Alerta onFechar={() => setErro('')}>{erro}</Alerta>
          <Campo rotulo="Usuário"><input value={login} onChange={(e) => setLogin(e.target.value)} autoComplete="username" required autoFocus /></Campo>
          <CampoSenha rotulo="Senha" value={senha} onChange={(e) => setSenha(e.target.value)} autoComplete="current-password" />
          <Botao className="bloco" carregando={enviando}>Entrar</Botao>
          <button type="button" className="btn fantasma bloco" onClick={onCadastrar}>Ainda não tenho conta — cadastrar-me</button>
        </div>
      </form>
    </section>
  </main>
}
