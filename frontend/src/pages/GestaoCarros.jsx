import { useState, useEffect, useCallback } from 'react'
import { api } from '../api'
import { Alerta, Botao, Campo, Modal, Spinner, Vazio, useToast, mascaraPlaca } from '../ui.jsx'

const vazio = () => ({ matricula: '', ano: new Date().getFullYear(), marca: '', modelo: '', placa: '', proprietarioId: '' })

export default function GestaoCarros() {
  const [carros, setCarros] = useState(null)
  const [donos, setDonos] = useState([])
  const [busca, setBusca] = useState('')
  const [erro, setErro] = useState('')
  const [modal, setModal] = useState(null) // {id?, f} | {excluir}
  const [erroModal, setErroModal] = useState('')
  const [env, setEnv] = useState(false)
  const toast = useToast()

  const carregar = useCallback(async () => {
    try {
      const [c, cl, em, bn] = await Promise.all([api.listarCarros(), api.listarClientes(), api.listarEmpresas(), api.listarBancos()])
      setCarros(c)
      setDonos([...em.map((x) => ({ id: x.id, nome: `${x.nome} (empresa)` })), ...bn.map((x) => ({ id: x.id, nome: `${x.nome} (banco)` })), ...cl.map((x) => ({ id: x.id, nome: `${x.nome} (cliente)` }))])
      setErro('')
    } catch (e) { setErro(e.message); setCarros([]) }
  }, [])
  useEffect(() => { carregar() }, [carregar])

  async function salvar(e) {
    e.preventDefault(); setErroModal(''); setEnv(true)
    const { id, f } = modal
    const corpo = { ...f, ano: Number(f.ano), placa: f.placa.toUpperCase().trim(), proprietarioId: Number(f.proprietarioId) }
    try {
      if (id) await api.atualizarCarro(id, corpo); else await api.cadastrarCarro(corpo)
      toast(id ? 'Automóvel atualizado.' : 'Automóvel cadastrado.'); setModal(null); await carregar()
    } catch (err) { setErroModal(err.message) } finally { setEnv(false) }
  }
  async function excluir() {
    setEnv(true)
    try { await api.excluirCarro(modal.excluir.id); toast('Automóvel excluído.'); setModal(null); await carregar() }
    catch (err) { setModal(null); setErro(err.message) } finally { setEnv(false) }
  }
  const lista = (carros || []).filter((c) => `${c.marca} ${c.modelo} ${c.placa} ${c.matricula}`.toLowerCase().includes(busca.toLowerCase()))
  const set = (k) => (e) => setModal({ ...modal, f: { ...modal.f, [k]: e.target.value } })

  return <main className="pagina">
    <div className="cab"><div><h2>Gestão de automóveis</h2><p>Cadastre, edite e remova veículos da frota.</p></div>
      <div style={{ display: 'flex', gap: 10 }}><input className="busca" placeholder="Buscar…" value={busca} onChange={(e) => setBusca(e.target.value)} />
        <button className="btn primario" onClick={() => { setErroModal(''); setModal({ f: vazio() }) }}>+ Novo automóvel</button></div></div>
    <Alerta onFechar={() => setErro('')}>{erro}</Alerta>
    {carros === null ? <Spinner /> : lista.length === 0 ? <Vazio icone="🚘" titulo="Nenhum automóvel" texto="Cadastre o primeiro veículo da frota." /> :
      <div className="cartao tabela-wrap"><table>
        <thead><tr><th>Veículo</th><th>Ano</th><th>Placa</th><th>Matrícula</th><th>Proprietário</th><th></th></tr></thead>
        <tbody>{lista.map((c) => <tr key={c.id}>
          <td><b>{c.marca} {c.modelo}</b></td><td>{c.ano}</td><td><span className="placa">{c.placa}</span></td><td>{c.matricula}</td><td>{c.proprietarioNome || '—'}</td>
          <td><div className="acoes">
            <button className="btn sec sm" onClick={() => { setErroModal(''); setModal({ id: c.id, f: { matricula: c.matricula, ano: c.ano, marca: c.marca, modelo: c.modelo, placa: c.placa, proprietarioId: c.proprietarioId ?? '' } }) }}>Editar</button>
            <button className="btn perigo sm" onClick={() => setModal({ excluir: c })}>Excluir</button></div></td></tr>)}</tbody></table></div>}

    {modal?.f && <Modal titulo={modal.id ? 'Editar automóvel' : 'Novo automóvel'} largo onFechar={() => setModal(null)}><form onSubmit={salvar}>
      <Alerta>{erroModal}</Alerta>
      <div className="form-grade">
        <Campo rotulo="Marca"><input required value={modal.f.marca} onChange={set('marca')} /></Campo>
        <Campo rotulo="Modelo"><input required value={modal.f.modelo} onChange={set('modelo')} /></Campo>
        <Campo rotulo="Ano"><input type="number" required min="1900" max="2100" value={modal.f.ano} onChange={set('ano')} /></Campo>
        <Campo rotulo="Placa"><input required maxLength={7} placeholder="ABC1D23" value={modal.f.placa} onChange={(e) => setModal({ ...modal, f: { ...modal.f, placa: mascaraPlaca(e.target.value) } })} /></Campo>
        <Campo rotulo="Matrícula"><input required value={modal.f.matricula} onChange={set('matricula')} /></Campo>
        <Campo rotulo="Proprietário"><select required value={modal.f.proprietarioId} onChange={set('proprietarioId')}>
          <option value="">Selecione…</option>{donos.map((d) => <option key={d.id} value={d.id}>{d.nome}</option>)}</select></Campo>
      </div>
      <div className="rodape-form"><button type="button" className="btn fantasma" onClick={() => setModal(null)}>Cancelar</button><Botao carregando={env}>Salvar</Botao></div>
    </form></Modal>}
    {modal?.excluir && <Modal titulo="Excluir automóvel" onFechar={() => setModal(null)}>
      <p>Excluir <b>{modal.excluir.marca} {modal.excluir.modelo}</b> ({modal.excluir.placa})? Veículos com pedidos associados não podem ser removidos.</p>
      <div className="rodape-form"><button className="btn fantasma" onClick={() => setModal(null)}>Cancelar</button><Botao variante="perigo" carregando={env} onClick={excluir}>Excluir</Botao></div>
    </Modal>}
  </main>
}
