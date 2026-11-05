import { useState, useEffect } from 'react'
import { api } from '../api'
import { Alerta, Botao, Campo, CampoSenha, cpfValido, mascaraCpf } from '../ui.jsx'
import { cnpjValido, mascaraCnpj } from '../utils.js'

export { cpfValido }

// Três perfis podem se cadastrar: cliente (pessoa física), empresa (locadora) e banco.
const PERFIS = [
  { id: 'cliente', titulo: 'Cliente', icone: '👤', texto: 'Quero alugar um automóvel' },
  { id: 'empresa', titulo: 'Empresa', icone: '🏢', texto: 'Sou locadora e avalio pedidos' },
  { id: 'banco', titulo: 'Banco', icone: '🏦', texto: 'Avalio pedidos e concedo crédito' },
]

const vazio = { nome: '', rg: '', cpf: '', endereco: '', profissao: '', login: '', senha: '', cnpj: '', razaoSocial: '', codigoBanco: '' }

export default function Cadastro({ onVoltar }) {
  const [perfil, setPerfil] = useState('cliente')
  const [agentesLiberados, setAgentesLiberados] = useState(true)
  const [f, setF] = useState(vazio)
  const [rend, setRend] = useState([{ entidadeEmpregadora: '', valor: '' }])
  const [erro, setErro] = useState('')
  const [ok, setOk] = useState(false)
  const [enviando, setEnviando] = useState(false)
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })
  const setR = (i, k, v) => setRend(rend.map((r, j) => (j === i ? { ...r, [k]: v } : r)))

  // O back-end informa se o autocadastro de empresas e bancos está liberado.
  useEffect(() => { api.cadastroAgentesPublico().then((r) => setAgentesLiberados(!!r?.habilitado)).catch(() => setAgentesLiberados(false)) }, [])

  const agente = perfil !== 'cliente'

  async function salvar(e) {
    e.preventDefault(); setErro('')
    if (f.senha.length < 6) return setErro('A senha deve ter ao menos 6 caracteres.')
    if (!agente && !cpfValido(f.cpf)) return setErro('CPF inválido. Confira os dígitos.')
    if (agente && !cnpjValido(f.cnpj)) return setErro('CNPJ inválido. Confira os dígitos.')
    if (perfil === 'banco' && !/^\d{3}$/.test(f.codigoBanco)) return setErro('O código do banco tem 3 dígitos (ex.: 001, 341).')
    setEnviando(true)
    try {
      const base = { nome: f.nome, login: f.login, senha: f.senha }
      if (perfil === 'cliente') {
        await api.cadastrarCliente({ ...base, rg: f.rg, cpf: f.cpf.replace(/\D/g, ''), endereco: f.endereco, profissao: f.profissao,
          rendimentos: rend.filter((r) => r.entidadeEmpregadora || r.valor).map((r) => ({ ...r, valor: Number(r.valor) })) })
      } else if (perfil === 'empresa') {
        await api.cadastrarEmpresa({ ...base, cnpj: f.cnpj.replace(/\D/g, ''), razaoSocial: f.razaoSocial })
      } else {
        await api.cadastrarBanco({ ...base, cnpj: f.cnpj.replace(/\D/g, ''), codigoBanco: f.codigoBanco })
      }
      setOk(true)
    } catch (err) { setErro(err.message) } finally { setEnviando(false) }
  }

  if (ok) return <main className="auth-form" style={{ minHeight: '70vh' }}><div className="cartao auth-card" style={{ textAlign: 'center' }}>
    <div style={{ fontSize: 48 }}>🎉</div><h2>Cadastro realizado!</h2>
    <p>{agente ? `Conta de ${perfil === 'banco' ? 'banco' : 'empresa'} criada. Entre para analisar os pedidos.` : 'Agora você já pode entrar e solicitar aluguéis.'}</p>
    <Botao className="bloco" onClick={onVoltar}>Ir para o login</Botao></div></main>

  return <main className="auth-form"><form className="cartao auth-card larga" onSubmit={salvar}>
    <h2>Criar conta</h2><p>Escolha o tipo de conta e preencha seus dados.</p>

    <div className="perfis" role="radiogroup" aria-label="Tipo de conta">
      {PERFIS.map((p) => {
        const bloqueado = p.id !== 'cliente' && !agentesLiberados
        return <button type="button" key={p.id} role="radio" aria-checked={perfil === p.id} disabled={bloqueado}
          className={'perfil-op' + (perfil === p.id ? ' ativo' : '')} onClick={() => { setPerfil(p.id); setErro('') }}>
          <span className="perfil-ic" aria-hidden="true">{p.icone}</span><b>{p.titulo}</b><small>{bloqueado ? 'Cadastro feito por um agente' : p.texto}</small>
        </button>
      })}
    </div>

    <Alerta onFechar={() => setErro('')}>{erro}</Alerta>
    <div className="form-grade">
      <Campo rotulo={agente ? 'Nome do responsável' : 'Nome completo'}><input value={f.nome} onChange={set('nome')} required /></Campo>
      {perfil === 'cliente' && <Campo rotulo="Profissão"><input value={f.profissao} onChange={set('profissao')} required /></Campo>}
      {perfil === 'empresa' && <Campo rotulo="Razão social"><input value={f.razaoSocial} onChange={set('razaoSocial')} required /></Campo>}
      {perfil === 'banco' && <Campo rotulo="Código do banco" dica="3 dígitos (ex.: 001, 341)"><input value={f.codigoBanco} onChange={(e) => setF({ ...f, codigoBanco: e.target.value.replace(/\D/g, '').slice(0, 3) })} required inputMode="numeric" placeholder="000" /></Campo>}

      {perfil === 'cliente' ? <>
        <Campo rotulo="RG"><input value={f.rg} onChange={set('rg')} required maxLength={20} /></Campo>
        <Campo rotulo="CPF"><input value={f.cpf} onChange={(e) => setF({ ...f, cpf: mascaraCpf(e.target.value) })} required placeholder="000.000.000-00" inputMode="numeric" /></Campo>
        <div className="inteira"><Campo rotulo="Endereço"><input value={f.endereco} onChange={set('endereco')} required /></Campo></div>
      </> : <div className="inteira"><Campo rotulo="CNPJ"><input value={f.cnpj} onChange={(e) => setF({ ...f, cnpj: mascaraCnpj(e.target.value) })} required placeholder="00.000.000/0000-00" inputMode="numeric" /></Campo></div>}

      <Campo rotulo="Usuário (login)"><input value={f.login} onChange={set('login')} required autoComplete="username" /></Campo>
      <CampoSenha rotulo="Senha" dica="Mínimo de 6 caracteres" value={f.senha} onChange={set('senha')} autoComplete="new-password" />
    </div>

    {perfil === 'cliente' && <>
      <h3 style={{ margin: '24px 0 12px' }}>Rendimentos <small style={{ fontWeight: 400, color: 'var(--mut)' }}>(até 3)</small></h3>
      {rend.map((r, i) => <div className="rend form-grade" key={i}>
        <Campo rotulo="Entidade empregadora"><input value={r.entidadeEmpregadora} onChange={(e) => setR(i, 'entidadeEmpregadora', e.target.value)} /></Campo>
        <Campo rotulo="Rendimento mensal (R$)"><input type="number" min="0.01" step="0.01" value={r.valor} onChange={(e) => setR(i, 'valor', e.target.value)} /></Campo>
        {rend.length > 1 && <button type="button" className="btn fantasma sm" onClick={() => setRend(rend.filter((_, j) => j !== i))}>Remover</button>}
      </div>)}
      {rend.length < 3 && <button type="button" className="btn sec sm" onClick={() => setRend([...rend, { entidadeEmpregadora: '', valor: '' }])}>+ Adicionar rendimento</button>}
    </>}

    <div className="rodape-form"><button type="button" className="btn fantasma" onClick={onVoltar}>Voltar ao login</button>
      <Botao carregando={enviando}>Cadastrar {PERFIS.find((p) => p.id === perfil).titulo.toLowerCase()}</Botao></div>
  </form></main>
}
