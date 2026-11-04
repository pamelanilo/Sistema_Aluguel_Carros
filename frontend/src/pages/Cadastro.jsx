import { useState } from 'react'
import { api } from '../api'

export default function Cadastro({ onVoltar }) {
  const [form, setForm] = useState({
    nome: '', rg: '', cpf: '', endereco: '', profissao: '', login: '', senha: '',
  })
  const [rendimentos, setRendimentos] = useState([{ entidadeEmpregadora: '', valor: '' }])
  const [erro, setErro] = useState('')
  const [ok, setOk] = useState(false)

  function mudar(campo, valor) { setForm({ ...form, [campo]: valor }) }

  function mudarRendimento(i, campo, valor) {
    const novo = [...rendimentos]
    novo[i] = { ...novo[i], [campo]: valor }
    setRendimentos(novo)
  }

  async function salvar(e) {
    e.preventDefault()
    setErro('')
    try {
      await api.cadastrarCliente({
        ...form,
        rendimentos: rendimentos.map((r) => ({ ...r, valor: Number(r.valor) })),
      })
      setOk(true)
    } catch (err) {
      setErro(err.message)
    }
  }

  if (ok) {
    return (
      <main className="pagina">
        <div className="caixa">
          <h2>Cadastro feito! 🎉</h2>
          <button className="btn-amarelo" onClick={onVoltar}>Ir para o login</button>
        </div>
      </main>
    )
  }

  return (
    <main className="pagina">
      <form className="caixa larga" onSubmit={salvar}>
        <h2>Criar conta de cliente</h2>
        <div className="grade">
          <div><label>Nome</label><input value={form.nome} onChange={(e) => mudar('nome', e.target.value)} required /></div>
          <div><label>Profissão</label><input value={form.profissao} onChange={(e) => mudar('profissao', e.target.value)} required /></div>
          <div><label>RG</label><input value={form.rg} onChange={(e) => mudar('rg', e.target.value)} required /></div>
          <div><label>CPF</label><input value={form.cpf} onChange={(e) => mudar('cpf', e.target.value)} required /></div>
          <div className="inteira"><label>Endereço</label><input value={form.endereco} onChange={(e) => mudar('endereco', e.target.value)} required /></div>
          <div><label>Usuário</label><input value={form.login} onChange={(e) => mudar('login', e.target.value)} required /></div>
          <div><label>Senha</label><input type="password" value={form.senha} onChange={(e) => mudar('senha', e.target.value)} required /></div>
        </div>

        <h3>Rendimentos (máximo 3)</h3>
        {rendimentos.map((r, i) => (
          <div className="grade" key={i}>
            <div><label>Empregador</label><input value={r.entidadeEmpregadora} onChange={(e) => mudarRendimento(i, 'entidadeEmpregadora', e.target.value)} required /></div>
            <div><label>Valor (R$)</label><input type="number" min="0" step="0.01" value={r.valor} onChange={(e) => mudarRendimento(i, 'valor', e.target.value)} required /></div>
          </div>
        ))}
        {rendimentos.length < 3 && (
          <button type="button" className="link" onClick={() => setRendimentos([...rendimentos, { entidadeEmpregadora: '', valor: '' }])}>
            + adicionar rendimento
          </button>
        )}

        {erro && <p className="erro">{erro}</p>}
        <button className="btn-amarelo" type="submit">Cadastrar</button>
        <button type="button" className="link" onClick={onVoltar}>Voltar</button>
      </form>
    </main>
  )
}
