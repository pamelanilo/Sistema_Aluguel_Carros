import { useState } from 'react'
import { api } from '../api'
import { Alerta, Botao, Campo, CampoSenha, cpfValido, mascaraCpf } from '../ui.jsx'

export { cpfValido }

const vazio = { nome: '', rg: '', cpf: '', endereco: '', profissao: '', login: '', senha: '' }

export default function Cadastro({ onVoltar }) {
  const [f, setF] = useState(vazio)
  const [rend, setRend] = useState([{ entidadeEmpregadora: '', valor: '' }])
  const [erro, setErro] = useState('')
  const [ok, setOk] = useState(false)
  const [enviando, setEnviando] = useState(false)
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })
  const setR = (i, k, v) => setRend(rend.map((r, j) => (j === i ? { ...r, [k]: v } : r)))

  async function salvar(e) {
    e.preventDefault(); setErro('')
    if (!cpfValido(f.cpf)) return setErro('CPF inválido. Confira os dígitos.')
    if (f.senha.length < 6) return setErro('A senha deve ter ao menos 6 caracteres.')
    setEnviando(true)
    try {
      await api.cadastrarCliente({ ...f, cpf: f.cpf.replace(/\D/g, ''), rendimentos: rend.filter((r) => r.entidadeEmpregadora || r.valor).map((r) => ({ ...r, valor: Number(r.valor) })) })
      setOk(true)
    } catch (err) { setErro(err.message) } finally { setEnviando(false) }
  }

  if (ok) return <main className="auth-form" style={{ minHeight: '70vh' }}><div className="cartao auth-card" style={{ textAlign: 'center' }}>
    <div style={{ fontSize: 48 }}>🎉</div><h2>Cadastro realizado!</h2><p>Agora você já pode entrar no sistema.</p>
    <Botao className="bloco" onClick={onVoltar}>Ir para o login</Botao></div></main>

  return <main className="auth-form"><form className="cartao auth-card larga" onSubmit={salvar}>
    <h2>Criar conta de cliente</h2><p>Preencha seus dados para solicitar aluguéis.</p>
    <Alerta onFechar={() => setErro('')}>{erro}</Alerta>
    <div className="form-grade">
      <Campo rotulo="Nome completo"><input value={f.nome} onChange={set('nome')} required /></Campo>
      <Campo rotulo="Profissão"><input value={f.profissao} onChange={set('profissao')} required /></Campo>
      <Campo rotulo="RG"><input value={f.rg} onChange={set('rg')} required maxLength={20} /></Campo>
      <Campo rotulo="CPF"><input value={f.cpf} onChange={(e) => setF({ ...f, cpf: mascaraCpf(e.target.value) })} required placeholder="000.000.000-00" inputMode="numeric" /></Campo>
      <div className="inteira"><Campo rotulo="Endereço"><input value={f.endereco} onChange={set('endereco')} required /></Campo></div>
      <Campo rotulo="Usuário (login)"><input value={f.login} onChange={set('login')} required autoComplete="username" /></Campo>
      <CampoSenha rotulo="Senha" dica="Mínimo de 6 caracteres" value={f.senha} onChange={set('senha')} autoComplete="new-password" />
    </div>
    <h3 style={{ margin: '24px 0 12px' }}>Rendimentos <small style={{ fontWeight: 400, color: 'var(--mut)' }}>(até 3)</small></h3>
    {rend.map((r, i) => <div className="rend form-grade" key={i}>
      <Campo rotulo="Entidade empregadora"><input value={r.entidadeEmpregadora} onChange={(e) => setR(i, 'entidadeEmpregadora', e.target.value)} /></Campo>
      <Campo rotulo="Rendimento mensal (R$)"><input type="number" min="0.01" step="0.01" value={r.valor} onChange={(e) => setR(i, 'valor', e.target.value)} /></Campo>
      {rend.length > 1 && <button type="button" className="btn fantasma sm" onClick={() => setRend(rend.filter((_, j) => j !== i))}>Remover</button>}
    </div>)}
    {rend.length < 3 && <button type="button" className="btn sec sm" onClick={() => setRend([...rend, { entidadeEmpregadora: '', valor: '' }])}>+ Adicionar rendimento</button>}
    <div className="rodape-form"><button type="button" className="btn fantasma" onClick={onVoltar}>Voltar ao login</button><Botao carregando={enviando}>Cadastrar</Botao></div>
  </form></main>
}
