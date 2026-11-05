import { useState, useEffect } from 'react'
import { api } from '../api'
import { Alerta, Botao, Campo, CampoSenha, Spinner, cpfValido, mascaraCpf, useToast } from '../ui.jsx'

// O cliente consulta e altera os próprios dados de identificação, profissão e rendimentos (máx. 3).
export default function MeusDados({ onAtualizado }) {
  const [f, setF] = useState(null)
  const [erro, setErro] = useState('')
  const [env, setEnv] = useState(false)
  const toast = useToast()

  async function carregar() {
    try {
      const c = await api.meusDados()
      setF({
        nome: c.nome || '', login: c.login || '', senha: '', rg: c.rg || '', cpf: mascaraCpf(c.cpf),
        endereco: c.endereco || '', profissao: c.profissao || '',
        rendimentos: (c.rendimentos || []).map((r) => ({ entidadeEmpregadora: r.entidadeEmpregadora, valor: r.valor })),
      })
    } catch (e) { setErro(e.message) }
  }
  useEffect(() => { carregar() }, [])

  if (!f) return <main className="pagina">{erro ? <Alerta>{erro}</Alerta> : <Spinner />}</main>

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })
  const setR = (i, k, v) => setF({ ...f, rendimentos: f.rendimentos.map((r, j) => (j === i ? { ...r, [k]: v } : r)) })

  async function salvar(e) {
    e.preventDefault(); setErro('')
    if (!cpfValido(f.cpf)) return setErro('CPF inválido. Confira os dígitos.')
    if (f.senha && f.senha.length < 6) return setErro('A nova senha deve ter ao menos 6 caracteres.')
    setEnv(true)
    try {
      await api.atualizarMeusDados({ ...f, cpf: f.cpf.replace(/\D/g, ''), senha: f.senha || null, rendimentos: f.rendimentos.map((r) => ({ ...r, valor: Number(r.valor) })) })
      toast('Seus dados foram atualizados.')
      await carregar()
      onAtualizado?.()
    } catch (err) { setErro(err.message) } finally { setEnv(false) }
  }

  return <main className="pagina">
    <div className="cab"><div><h2>Meus dados</h2><p>Mantenha seus dados e rendimentos atualizados: eles são usados na análise financeira dos pedidos.</p></div></div>
    <form className="cartao" style={{ padding: 24 }} onSubmit={salvar}>
      <Alerta onFechar={() => setErro('')}>{erro}</Alerta>
      <div className="form-grade">
        <Campo rotulo="Nome completo"><input required value={f.nome} onChange={set('nome')} /></Campo>
        <Campo rotulo="Profissão"><input required value={f.profissao} onChange={set('profissao')} /></Campo>
        <Campo rotulo="RG"><input required maxLength={20} value={f.rg} onChange={set('rg')} /></Campo>
        <Campo rotulo="CPF"><input required inputMode="numeric" placeholder="000.000.000-00" value={f.cpf} onChange={(e) => setF({ ...f, cpf: mascaraCpf(e.target.value) })} /></Campo>
        <div className="inteira"><Campo rotulo="Endereço"><input required value={f.endereco} onChange={set('endereco')} /></Campo></div>
        <Campo rotulo="Usuário (login)"><input required value={f.login} onChange={set('login')} autoComplete="username" /></Campo>
        <CampoSenha rotulo="Nova senha (opcional)" dica="Deixe em branco para manter a atual" required={false} value={f.senha} onChange={set('senha')} autoComplete="new-password" />
      </div>
      <h3 style={{ margin: '24px 0 12px' }}>Rendimentos <small style={{ fontWeight: 400, color: 'var(--mut)' }}>(até 3)</small></h3>
      {f.rendimentos.map((r, i) => <div className="rend form-grade" key={i}>
        <Campo rotulo="Entidade empregadora"><input required value={r.entidadeEmpregadora} onChange={(e) => setR(i, 'entidadeEmpregadora', e.target.value)} /></Campo>
        <Campo rotulo="Rendimento mensal (R$)"><input required type="number" min="0.01" step="0.01" value={r.valor} onChange={(e) => setR(i, 'valor', e.target.value)} /></Campo>
        <button type="button" className="btn fantasma sm" onClick={() => setF({ ...f, rendimentos: f.rendimentos.filter((_, j) => j !== i) })}>Remover</button>
      </div>)}
      {f.rendimentos.length < 3 && <button type="button" className="btn sec sm" onClick={() => setF({ ...f, rendimentos: [...f.rendimentos, { entidadeEmpregadora: '', valor: '' }] })}>+ Adicionar rendimento</button>}
      <div className="rodape-form"><Botao carregando={env}>Salvar alterações</Botao></div>
    </form>
  </main>
}
