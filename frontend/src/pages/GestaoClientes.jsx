import { useState, useEffect, useCallback } from 'react'
import { api } from '../api'
import { Alerta, Botao, Campo, CampoSenha, Modal, Spinner, Vazio, brl, useToast, cpfValido, mascaraCpf, fmtCpf } from '../ui.jsx'

const vazio = () => ({ nome: '', login: '', senha: '', rg: '', cpf: '', endereco: '', profissao: '', rendimentos: [] })

export default function GestaoClientes() {
  const [clientes, setClientes] = useState(null)
  const [busca, setBusca] = useState('')
  const [erro, setErro] = useState('')
  const [modal, setModal] = useState(null)
  const [erroModal, setErroModal] = useState('')
  const [env, setEnv] = useState(false)
  const toast = useToast()

  const carregar = useCallback(async () => {
    try { setClientes(await api.listarClientes()); setErro('') } catch (e) { setErro(e.message); setClientes([]) }
  }, [])
  useEffect(() => { carregar() }, [carregar])

  const set = (k) => (e) => setModal({ ...modal, f: { ...modal.f, [k]: e.target.value } })
  const setR = (i, k, v) => setModal({ ...modal, f: { ...modal.f, rendimentos: modal.f.rendimentos.map((r, j) => (j === i ? { ...r, [k]: v } : r)) } })

  async function salvar(e) {
    e.preventDefault(); setErroModal('')
    const { id, f } = modal
    if (!cpfValido(f.cpf)) return setErroModal('CPF inválido.')
    if (!id && f.senha.length < 6) return setErroModal('A senha deve ter ao menos 6 caracteres.')
    setEnv(true)
    const corpo = { ...f, cpf: f.cpf.replace(/\D/g, ''), rendimentos: f.rendimentos.map((r) => ({ ...r, valor: Number(r.valor) })) }
    try {
      if (id) await api.atualizarCliente(id, corpo); else await api.cadastrarCliente(corpo)
      toast(id ? 'Cliente atualizado.' : 'Cliente cadastrado.'); setModal(null); await carregar()
    } catch (err) { setErroModal(err.message) } finally { setEnv(false) }
  }
  async function excluir() {
    setEnv(true)
    try { await api.excluirCliente(modal.excluir.id); toast('Cliente excluído.'); setModal(null); await carregar() }
    catch (err) { setModal(null); setErro(err.message) } finally { setEnv(false) }
  }
  const lista = (clientes || []).filter((c) => `${c.nome} ${c.cpf} ${c.login}`.toLowerCase().includes(busca.toLowerCase()))

  return <main className="pagina">
    <div className="cab"><div><h2>Gestão de clientes</h2><p>Consulte, edite e remova cadastros.</p></div>
      <div style={{ display: 'flex', gap: 10 }}><input className="busca" placeholder="Buscar nome, CPF ou login…" value={busca} onChange={(e) => setBusca(e.target.value)} />
        <button className="btn primario" onClick={() => { setErroModal(''); setModal({ f: vazio() }) }}>+ Novo cliente</button></div></div>
    <Alerta onFechar={() => setErro('')}>{erro}</Alerta>
    {clientes === null ? <Spinner /> : lista.length === 0 ? <Vazio icone="👤" titulo="Nenhum cliente encontrado" /> :
      <div className="cartao tabela-wrap"><table>
        <thead><tr><th>Nome</th><th>CPF</th><th>Login</th><th>Profissão</th><th>Rendimentos</th><th></th></tr></thead>
        <tbody>{lista.map((c) => <tr key={c.id}>
          <td><b>{c.nome}</b></td><td>{fmtCpf(c.cpf)}</td><td>{c.login}</td><td>{c.profissao}</td>
          <td>{(c.rendimentos || []).length ? (c.rendimentos).map((r) => `${r.entidadeEmpregadora}: ${brl(r.valor)}`).join(' · ') : '—'}</td>
          <td><div className="acoes">
            <button className="btn sec sm" onClick={() => { setErroModal(''); setModal({ id: c.id, f: { nome: c.nome || '', login: c.login || '', senha: '', rg: c.rg || '', cpf: c.cpf || '', endereco: c.endereco || '', profissao: c.profissao || '', rendimentos: (c.rendimentos || []).map((r) => ({ entidadeEmpregadora: r.entidadeEmpregadora, valor: r.valor })) } }) }}>Editar</button>
            <button className="btn perigo sm" onClick={() => setModal({ excluir: c })}>Excluir</button></div></td></tr>)}</tbody></table></div>}

    {modal?.f && <Modal titulo={modal.id ? 'Editar cliente' : 'Novo cliente'} largo onFechar={() => setModal(null)}><form onSubmit={salvar}>
      <Alerta>{erroModal}</Alerta>
      <div className="form-grade">
        <Campo rotulo="Nome"><input required value={modal.f.nome} onChange={set('nome')} /></Campo>
        <Campo rotulo="Profissão"><input required value={modal.f.profissao} onChange={set('profissao')} /></Campo>
        <Campo rotulo="RG"><input required value={modal.f.rg} onChange={set('rg')} /></Campo>
        <Campo rotulo="CPF"><input required placeholder="000.000.000-00" inputMode="numeric" value={mascaraCpf(modal.f.cpf)} onChange={(e) => setModal({ ...modal, f: { ...modal.f, cpf: mascaraCpf(e.target.value) } })} /></Campo>
        <div className="inteira"><Campo rotulo="Endereço"><input required value={modal.f.endereco} onChange={set('endereco')} /></Campo></div>
        <Campo rotulo="Login"><input required value={modal.f.login} onChange={set('login')} /></Campo>
        <CampoSenha rotulo={modal.id ? 'Nova senha (opcional)' : 'Senha'} required={!modal.id} value={modal.f.senha} onChange={set('senha')} autoComplete="new-password" />
      </div>
      <h4 style={{ margin: '20px 0 10px' }}>Rendimentos (máx. 3)</h4>
      {modal.f.rendimentos.map((r, i) => <div className="rend form-grade" key={i}>
        <Campo rotulo="Empregador"><input required value={r.entidadeEmpregadora} onChange={(e) => setR(i, 'entidadeEmpregadora', e.target.value)} /></Campo>
        <Campo rotulo="Valor (R$)"><input required type="number" min="0.01" step="0.01" value={r.valor} onChange={(e) => setR(i, 'valor', e.target.value)} /></Campo>
        <button type="button" className="btn fantasma sm" onClick={() => setModal({ ...modal, f: { ...modal.f, rendimentos: modal.f.rendimentos.filter((_, j) => j !== i) } })}>Remover</button></div>)}
      {modal.f.rendimentos.length < 3 && <button type="button" className="btn sec sm" onClick={() => setModal({ ...modal, f: { ...modal.f, rendimentos: [...modal.f.rendimentos, { entidadeEmpregadora: '', valor: '' }] } })}>+ Rendimento</button>}
      <div className="rodape-form"><button type="button" className="btn fantasma" onClick={() => setModal(null)}>Cancelar</button><Botao carregando={env}>Salvar</Botao></div>
    </form></Modal>}
    {modal?.excluir && <Modal titulo="Excluir cliente" onFechar={() => setModal(null)}>
      <p>Excluir o cadastro de <b>{modal.excluir.nome}</b>? Clientes com pedidos ou automóveis associados podem não ser removidos.</p>
      <div className="rodape-form"><button className="btn fantasma" onClick={() => setModal(null)}>Cancelar</button><Botao variante="perigo" carregando={env} onClick={excluir}>Excluir</Botao></div>
    </Modal>}
  </main>
}
