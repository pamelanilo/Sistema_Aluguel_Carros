import { createContext, useContext, useState, useCallback, useEffect } from 'react'

export { cpfValido, mascaraCpf, mascaraPlaca, fmtCpf } from './utils.js'

export const brl = (v) => v == null ? '—' : Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
export const dataBr = (d) => d ? d.split('-').reverse().join('/') : '—'

export const ROTULO = {
  PENDENTE: 'Pendente', EM_ANALISE: 'Em análise', APROVADO: 'Aprovado', REPROVADO: 'Recusado',
  CANCELADO: 'Cancelado', EXECUTADO: 'Executado', ASSINADO: 'Assinado',
  PROPRIEDADE_CLIENTE: 'Propriedade do cliente', PROPRIEDADE_EMPRESA: 'Propriedade da empresa',
  PROPRIEDADE_BANCO: 'Propriedade do banco',
}

export function Badge({ valor }) {
  return <span className={'badge b-' + String(valor).toLowerCase()}>{ROTULO[valor] || valor}</span>
}

export function Spinner({ texto = 'Carregando…' }) {
  return <div className="spinner-box"><span className="spinner" />{texto}</div>
}

export function Vazio({ icone = '📭', titulo, texto }) {
  return <div className="vazio"><div className="vazio-i">{icone}</div><h3>{titulo}</h3>{texto && <p>{texto}</p>}</div>
}

export function Alerta({ tipo = 'erro', children, onFechar }) {
  if (!children) return null
  return <div className={'alerta a-' + tipo} role="alert"><span>{children}</span>{onFechar && <button onClick={onFechar} aria-label="Fechar">×</button>}</div>
}

export function Modal({ titulo, onFechar, children, largo }) {
  useEffect(() => {
    const f = (e) => e.key === 'Escape' && onFechar()
    window.addEventListener('keydown', f)
    return () => window.removeEventListener('keydown', f)
  }, [onFechar])
  return <div className="overlay" onMouseDown={onFechar}>
    <div className={'modal' + (largo ? ' largo' : '')} role="dialog" aria-modal="true" onMouseDown={(e) => e.stopPropagation()}>
      <div className="modal-topo"><h3>{titulo}</h3><button className="x" onClick={onFechar} aria-label="Fechar">×</button></div>
      <div className="modal-corpo">{children}</div>
    </div>
  </div>
}

export function Botao({ carregando, children, variante = 'primario', ...p }) {
  return <button {...p} className={`btn ${variante} ${p.className || ''}`} disabled={carregando || p.disabled}>
    {carregando && <span className="spinner sm" />}{children}
  </button>
}

export function Campo({ rotulo, children, dica }) {
  return <label className="campo"><span>{rotulo}</span>{children}{dica && <small>{dica}</small>}</label>
}

const ToastCtx = createContext(() => {})
export const useToast = () => useContext(ToastCtx)
export function ToastProvider({ children }) {
  const [itens, setItens] = useState([])
  const mostrar = useCallback((texto, tipo = 'sucesso') => {
    const id = Math.random()
    setItens((x) => [...x, { id, texto, tipo }])
    setTimeout(() => setItens((x) => x.filter((i) => i.id !== id)), 4000)
  }, [])
  return <ToastCtx.Provider value={mostrar}>
    {children}
    <div className="toasts">{itens.map((i) => <div key={i.id} className={'toast t-' + i.tipo}>{i.texto}</div>)}</div>
  </ToastCtx.Provider>
}

export function Logo() {
  return <span className="logo">
    <svg viewBox="0 0 32 32" width="32" height="32" aria-hidden="true">
      <defs><linearGradient id="lg" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stopColor="#2f7df6" /><stop offset="1" stopColor="#14b8a6" /></linearGradient></defs>
      <rect width="32" height="32" rx="9" fill="url(#lg)" />
      <path d="M8 10.5 16 23l8-12.5" fill="none" stroke="#fff" strokeWidth="3.2" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M4.5 16h3M3 20h4" stroke="#fff" strokeOpacity=".55" strokeWidth="1.6" strokeLinecap="round" />
    </svg>
    <b>VRUM<span>!</span></b>
  </span>
}

// Ilustração do automóvel em SVG; a cor varia conforme o modelo/placa para diferenciar os cards.
export function CarroArte({ seed = '' }) {
  const h = [...String(seed)].reduce((a, c) => a + c.charCodeAt(0), 0)
  const cor = ['#2f7df6', '#14b8a6', '#6ea8ff', '#5eead4', '#8aa4ff'][h % 5]
  return <svg viewBox="0 0 240 110" className="carro-svg" aria-hidden="true">
    <ellipse cx="120" cy="98" rx="100" ry="6" fill="rgba(0,0,0,.28)" />
    <path d="M22 76c0-10 8-14 20-16l24-20c6-6 14-8 24-8h58c10 0 18 3 26 10l18 18c14 2 26 6 26 16v8H22z" fill={cor} />
    <path d="M74 44l18-6h26v22H58z" fill="#0a1f3d" opacity=".55" />
    <path d="M126 38h24c6 0 10 2 15 7l13 15h-52z" fill="#0a1f3d" opacity=".55" />
    <circle cx="70" cy="84" r="15" fill="#06142a" /><circle cx="70" cy="84" r="6.5" fill="#cbd5e1" />
    <circle cx="170" cy="84" r="15" fill="#06142a" /><circle cx="170" cy="84" r="6.5" fill="#cbd5e1" />
  </svg>
}

export function CampoSenha({ rotulo, dica, value, onChange, autoComplete, required = true }) {
  const [ver, setVer] = useState(false)
  return <label className="campo"><span>{rotulo}</span>
    <div className="senha-wrap">
      <input type={ver ? 'text' : 'password'} value={value} onChange={onChange} autoComplete={autoComplete} required={required} />
      <button type="button" className="ver" onClick={() => setVer(!ver)} aria-label={ver ? 'Ocultar senha' : 'Mostrar senha'}>{ver ? 'Ocultar' : 'Mostrar'}</button>
    </div>{dica && <small>{dica}</small>}</label>
}
