import { useState } from 'react'
import { api } from '../api'

export default function Login({ onEntrar, onCadastrar }) {
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')

  async function entrar(e) {
    e.preventDefault() // impede a página de recarregar
    setErro('')
    try {
      await api.login(login, senha)
      const usuario = await api.eu() // descobre quem entrou (id, nome, tipo)
      onEntrar(usuario)
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <main>
      <section className="hero">
        <div>
          <h1>Viaje com liberdade.<br />Alugue um carro com o Vrum!</h1>
          <p>Escolha o carro, faça o pedido e acompanhe a análise em um só lugar.</p>
        </div>
        <form className="caixa" onSubmit={entrar}>
          <h2>Entrar</h2>
          <label>Usuário</label>
          <input value={login} onChange={(e) => setLogin(e.target.value)} required />
          <label>Senha</label>
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} required />
          {erro && <p className="erro">{erro}</p>}
          <button className="btn-amarelo" type="submit">Entrar</button>
          <button type="button" className="link" onClick={onCadastrar}>Ainda não tenho conta</button>
        </form>
      </section>
    </main>
  )
}
