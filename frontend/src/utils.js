// Funções de apoio: máscaras de entrada e validações usadas nos formulários.
export const soDigitos = (v) => (v || '').replace(/\D/g, '')

export function mascaraCpf(v) {
  return soDigitos(v).slice(0, 11)
    .replace(/^(\d{3})(\d)/, '$1.$2')
    .replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/\.(\d{3})(\d)/, '.$1-$2')
}

export const mascaraPlaca = (v) => (v || '').toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 7)

export const fmtCpf = (c) => mascaraCpf(c)

export function cpfValido(v) {
  const d = soDigitos(v)
  if (d.length !== 11 || /^(\d)\1+$/.test(d)) return false
  for (let t = 9; t < 11; t++) {
    let s = 0
    for (let i = 0; i < t; i++) s += Number(d[i]) * (t + 1 - i)
    if (((s * 10) % 11) % 10 !== Number(d[t])) return false
  }
  return true
}

export function mascaraCnpj(v) {
  return soDigitos(v).slice(0, 14)
    .replace(/^(\d{2})(\d)/, '$1.$2')
    .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/\.(\d{3})(\d)/, '.$1/$2')
    .replace(/(\d{4})(\d)/, '$1-$2')
}

export function cnpjValido(v) {
  const d = soDigitos(v)
  if (d.length !== 14 || /^(\d)\1+$/.test(d)) return false
  const calc = (n) => {
    const pesos = n === 12 ? [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2] : [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
    const r = pesos.reduce((s, p, i) => s + Number(d[i]) * p, 0) % 11
    return r < 2 ? 0 : 11 - r
  }
  return calc(12) === Number(d[12]) && calc(13) === Number(d[13])
}
