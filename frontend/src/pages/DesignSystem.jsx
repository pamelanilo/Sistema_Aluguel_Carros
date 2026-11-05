import { useState } from 'react'
import { Alerta, Badge, Botao, Campo, CampoSenha, CarroArte, Logo, Modal, Spinner, Vazio, mascaraCpf, mascaraPlaca, useToast } from '../ui.jsx'

// Documentação viva do design system do VRUM!: todos os exemplos abaixo usam os
// mesmos componentes (ui.jsx) e tokens (style.css) das telas reais.

const ABAS = [
  ['principios', 'Princípios'],
  ['cores', 'Cores'],
  ['tipografia', 'Tipografia'],
  ['espacos', 'Espaçamento e forma'],
  ['componentes', 'Componentes'],
  ['status', 'Status do pedido'],
  ['padroes', 'Padrões de tela'],
  ['acessibilidade', 'Acessibilidade'],
]

const CORES = [
  { grupo: 'Marca — base escura', itens: [
    ['--navy-950', '#06142a', 'Fundo do topo e do herói de login (início do gradiente).'],
    ['--navy-900', '#0a1f3d', 'Cartão de destaque e ilustração dos carros.'],
    ['--navy-800', '#0f2c52', 'Fim do gradiente do topo, rótulos de campo, chip ativo.'],
    ['--navy-700', '#163a68', 'Fim dos gradientes de cartões em destaque.'],
  ] },
  { grupo: 'Marca — ação', itens: [
    ['--blue', '#2f7df6', 'Ação principal (botão primário), foco e bordas ativas.'],
    ['--blue-d', '#1f64d6', 'Fim do gradiente do primário; links e "Mostrar senha".'],
    ['--teal', '#14b8a6', 'Acento da marca ("!" do logo), progresso concluído na trilha.'],
    ['--teal-d', '#0d9488', 'Ações positivas (Aprovar, Iniciar análise).'],
  ] },
  { grupo: 'Neutros', itens: [
    ['--bg', '#eef3f9', 'Fundo da aplicação: azul-acinzentado que separa os cartões.'],
    ['--card', '#ffffff', 'Superfície de cartões, modais e formulários.'],
    ['--txt', '#0f1f35', 'Texto principal (quase-navy, nunca preto puro).'],
    ['--mut', '#5b6b82', 'Texto secundário, dicas e metadados.'],
    ['--line', '#dce5f0', 'Bordas, divisórias e contorno de campos.'],
  ] },
  { grupo: 'Feedback', itens: [
    ['--ok', '#0f9d6b', 'Sucesso (toast, confirmações).'],
    ['--warn', '#d98a0b', 'Atenção (reservado para avisos).'],
    ['--bad', '#d64550', 'Erro, recusa e ações destrutivas.'],
  ] },
]

const CONTRASTE = [
  ['Texto principal sobre o fundo', '#0f1f35', '#eef3f9', 14.8],
  ['Texto principal sobre cartão', '#0f1f35', '#ffffff', 16.6],
  ['Texto secundário sobre cartão', '#5b6b82', '#ffffff', 5.4],
  ['Texto secundário sobre o fundo', '#5b6b82', '#eef3f9', 4.9],
  ['Branco sobre navy-800', '#ffffff', '#0f2c52', 14.0],
  ['Teal-claro sobre navy-900 (número em destaque)', '#5eead4', '#0a1f3d', 11.1],
  ['Badge "Pendente"', '#8a5a00', '#fff3d6', 5.4],
  ['Badge "Em análise"', '#1c4e9f', '#e0ecff', 6.7],
  ['Badge "Aprovado"', '#0b6e4b', '#dff7ec', 5.6],
  ['Badge "Recusado"', '#a42431', '#fde3e5', 6.0],
  ['Badge "Cancelado"', '#5b6b82', '#e9edf3', 4.6],
  ['Branco sobre azul (botão primário)', '#ffffff', '#2f7df6', 3.9],
  ['Branco sobre teal (botão teal)', '#ffffff', '#14b8a6', 2.5],
  ['Vermelho sobre rosa (botão perigo)', '#c2343f', '#fdecee', 4.8],
]

const CONTRASTE_ESCURO = [
  ['Texto principal sobre o fundo', '#e4ecf7', '#0a1322', 15.6],
  ['Texto principal sobre cartão', '#e4ecf7', '#111c30', 14.3],
  ['Texto secundário sobre cartão', '#9fb0c8', '#111c30', 7.7],
  ['Links sobre cartão', '#8ab8ff', '#111c30', 8.4],
  ['Badge "Pendente"', '#ffd27a', '#382a0b', 9.8],
  ['Badge "Em análise"', '#a9cbff', '#142a4d', 8.6],
  ['Badge "Aprovado"', '#82e3bb', '#0f2d24', 9.6],
  ['Badge "Recusado"', '#ffb8bf', '#3a1a22', 9.5],
  ['Badge "Cancelado"', '#9fb0c8', '#222f45', 6.1],
  ['Botão perigo', '#ff9aa3', '#3a1a22', 7.7],
  ['Chip ativo (branco sobre azul)', '#ffffff', '#1f64d6', 5.5],
]

const TEMA = [
  ['--bg', '#eef3f9', '#0a1322', 'Fundo da aplicação'],
  ['--card', '#ffffff', '#111c30', 'Cartões, modais'],
  ['--field', '#ffffff', '#0d1729', 'Campos de formulário'],
  ['--surface-2', '#f6f9fd', '#16243b', 'Áreas internas (parecer, resumo, cabeçalho de tabela)'],
  ['--txt', '#0f1f35', '#e4ecf7', 'Texto principal'],
  ['--mut', '#5b6b82', '#9fb0c8', 'Texto secundário'],
  ['--line', '#dce5f0', '#26364f', 'Bordas'],
  ['--heading', '#0f2c52', '#cfe0ff', 'Rótulos, números de destaque, placas'],
  ['--link', '#1f64d6', '#8ab8ff', 'Links e "Mostrar senha"'],
  ['--info-bg / --info-txt', '#e0ecff', '#142a4d', 'Badge "Em análise", alerta informativo'],
  ['--ok-bg / --ok-txt', '#dff7ec', '#0f2d24', 'Badge "Aprovado", alerta de sucesso'],
  ['--danger-bg / --danger-txt', '#fdecee', '#3a1a22', 'Erros, recusa, botão perigo'],
]

function Secao({ titulo, children, texto }) {
  return <section className="ds-secao">
    <h3>{titulo}</h3>
    {texto && <p className="ds-texto">{texto}</p>}
    {children}
  </section>
}

function Exemplo({ children, codigo, rotulo }) {
  return <div className="ds-exemplo">
    {rotulo && <div className="ds-rotulo">{rotulo}</div>}
    <div className="ds-palco">{children}</div>
    {codigo && <pre className="ds-codigo"><code>{codigo}</code></pre>}
  </div>
}

export default function DesignSystem() {
  const [aba, setAba] = useState('principios')
  return <main className="pagina">
    <div className="cab"><div>
      <h2>Design System VRUM!</h2>
      <p>Cores, tipografia, componentes e padrões usados em todas as telas, com o motivo de cada escolha.</p>
    </div></div>
    <div className="ds-abas" role="tablist" aria-label="Seções do design system">
      {ABAS.map(([id, rotulo]) => <button key={id} role="tab" aria-selected={aba === id}
        className={'chip' + (aba === id ? ' ativo' : '')} onClick={() => setAba(id)}>{rotulo}</button>)}
    </div>
    <div className="cartao ds-conteudo" role="tabpanel">
      {aba === 'principios' && <Principios />}
      {aba === 'cores' && <Cores />}
      {aba === 'tipografia' && <Tipografia />}
      {aba === 'espacos' && <Espacos />}
      {aba === 'componentes' && <Componentes />}
      {aba === 'status' && <Status />}
      {aba === 'padroes' && <Padroes />}
      {aba === 'acessibilidade' && <Acessibilidade />}
    </div>
  </main>
}

function Principios() {
  const p = [
    ['🧭', 'Clareza do estado', 'Aluguel envolve espera (análise financeira). Todo pedido mostra o estado com badge colorido e uma trilha de progresso: o cliente sempre sabe em que etapa está.'],
    ['🔒', 'Confiança', 'Base navy (cor associada a bancos e instituições) transmite segurança para dados pessoais e financeiros; o azul e o teal trazem energia sem perder seriedade.'],
    ['🎯', 'Uma ação principal por contexto', 'Cada tela ou modal tem um único botão primário (azul). As demais ações usam variantes secundárias, fantasma ou perigo, de menor peso visual.'],
    ['🧩', 'Componentes reutilizáveis', 'Botões, campos, alertas, badges e modais vêm de um único arquivo (ui.jsx) e de tokens CSS (style.css). Mudar um token atualiza o sistema inteiro.'],
    ['♿', 'Acessível por padrão', 'Foco visível em todos os controles, rótulos ligados aos campos, modais que fecham com Esc, respeito a "reduzir movimento" e layout que funciona no celular.'],
    ['🗣️', 'Linguagem do usuário', 'Textos em português simples ("Recusado", não "REPROVADO"), mensagens de erro que dizem o que corrigir e confirmação antes de ações irreversíveis.'],
  ]
  return <Secao titulo="Princípios de design" texto="Seis decisões guiam todas as telas do sistema. Quando houver dúvida em uma tela nova, volte a elas.">
    <div className="ds-grade">{p.map(([i, t, d]) => <div key={t} className="ds-principio">
      <div className="ds-icone" aria-hidden="true">{i}</div><b>{t}</b><p>{d}</p></div>)}</div>
    <Exemplo rotulo="Identidade: logotipo">
      <div className="ds-logo-fundo"><Logo /></div>
      <p className="ds-texto" style={{ margin: 0 }}>O "V" do símbolo lembra uma seta de movimento; o gradiente azul → teal é o mesmo das ações da interface, e as linhas à esquerda sugerem velocidade.</p>
    </Exemplo>
  </Secao>
}

function Cores() {
  return <>
    {CORES.map((g) => <Secao key={g.grupo} titulo={g.grupo} texto={g.grupo === "Neutros" ? "Valores do tema claro; os equivalentes do modo escuro estão na tabela abaixo." : undefined}>
      <div className="ds-cores">{g.itens.map(([token, hex, uso]) => <div key={token} className="ds-cor">
        <div className="ds-amostra" style={{ background: hex }} />
        <div><code>{token}</code><span className="ds-hex">{hex}</span><p>{uso}</p></div>
      </div>)}</div>
    </Secao>)}
    <Secao titulo="Modo escuro" texto="Cada token tem um valor claro e um escuro. O tema segue o sistema operacional (prefers-color-scheme) e pode ser trocado pelo botão ☀️/🌙 no topo; a escolha fica salva neste navegador. Componentes nunca usam cores fixas: só tokens, por isso funcionam nos dois temas sem código extra.">
      <div className="cartao tabela-wrap"><table>
        <thead><tr><th>Token</th><th>Claro</th><th>Escuro</th><th>Uso</th></tr></thead>
        <tbody>{TEMA.map(([t, c, e, u]) => <tr key={t}>
          <td><code>{t}</code></td>
          <td><span className="ds-par"><i style={{ background: c }} />{c}</span></td>
          <td><span className="ds-par"><i style={{ background: e }} />{e}</span></td>
          <td className="meta">{u}</td></tr>)}</tbody>
      </table></div>
      <p className="ds-texto" style={{ marginTop: 14 }}>O topo, o herói do login e as ilustrações já são escuros nos dois temas; no tema escuro as sombras ficam mais fortes e as cores de estado ganham versões claras sobre fundos escuros, mantendo o contraste.</p>
    </Secao>
    <Secao titulo="Gradientes" texto="Gradientes diagonais (135°) dão profundidade aos elementos de destaque sem precisar de imagens.">
      <div className="ds-cores">
        <div className="ds-cor"><div className="ds-amostra" style={{ background: 'linear-gradient(90deg,var(--navy-950),var(--navy-800))' }} /><div><code>topo</code><p>navy-950 → navy-800, barra de navegação.</p></div></div>
        <div className="ds-cor"><div className="ds-amostra" style={{ background: 'linear-gradient(135deg,var(--blue),var(--blue-d))' }} /><div><code>.btn.primario</code><p>blue → blue-d, ação principal.</p></div></div>
        <div className="ds-cor"><div className="ds-amostra" style={{ background: 'linear-gradient(135deg,var(--navy-900),var(--navy-700) 60%,var(--teal-d))' }} /><div><code>.carro-img</code><p>Fundo das ilustrações do catálogo.</p></div></div>
      </div>
    </Secao>
  </>
}

function Tipografia() {
  const escala = [
    ['Título do herói', 42, 800, 'Alugue o carro certo', 'h1 · login'],
    ['Número em destaque', 34, 800, '128', '.stat strong · Início'],
    ['Título de página', 28, 700, 'Meus pedidos', '.cab h2'],
    ['Título de cartão', 18, 700, 'Fiat Argo 2024', '.carro-corpo h3'],
    ['Corpo', 15, 400, 'Acompanhe o andamento das suas solicitações.', 'body'],
    ['Rótulo / botão', 14, 700, 'Solicitar aluguel', '.btn · .campo'],
    ['Metadado', 13, 400, 'Retirada 10/11 · Devolução 15/11', '.meta · .info'],
    ['Badge / cabeçalho de tabela', 12, 700, 'EM ANÁLISE', '.badge · th'],
  ]
  return <>
    <Secao titulo="Família tipográfica" texto="Plus Jakarta Sans (com fallback para a fonte do sistema): geométrica, moderna e muito legível em números, o que importa em datas, valores e placas. Títulos usam espaçamento entre letras levemente negativo (-0,01em) para ficarem mais compactos.">
      <div className="ds-especime">Aa Bb Cc 0123456789 R$ 1.250,00</div>
    </Secao>
    <Secao titulo="Escala" texto="Poucos tamanhos, com saltos claros entre níveis. O peso (400 → 800) reforça a hierarquia tanto quanto o tamanho.">
      <div className="ds-escala">{escala.map(([nome, tam, peso, ex, onde]) => <div key={nome} className="ds-linha-tipo">
        <div className="ds-meta-tipo"><b>{nome}</b><span>{tam}px · peso {peso}</span><code>{onde}</code></div>
        <div style={{ fontSize: tam, fontWeight: peso, lineHeight: 1.2 }}>{ex}</div>
      </div>)}</div>
    </Secao>
    <Secao titulo="Monoespaçada para placas" texto="Placas usam fonte monoespaçada, espaçamento largo e borda, imitando a placa real; assim são reconhecidas de relance.">
      <span className="placa">ABC1D23</span>
    </Secao>
  </>
}

function Espacos() {
  const raios = [['Badge / chip', 99], ['Placa', 6], ['Botão / campo / alerta', 10], ['Cartão (--r)', 14], ['Modal', 18]]
  const esp = [6, 8, 12, 16, 20, 28]
  return <>
    <Secao titulo="Espaçamento" texto="Múltiplos aproximados de 4px. Elementos relacionados ficam mais próximos (6–12px); blocos independentes, mais afastados (16–28px).">
      <div className="ds-espacos">{esp.map((e) => <div key={e} className="ds-esp"><div style={{ width: e, height: e }} /><span>{e}px</span></div>)}</div>
    </Secao>
    <Secao titulo="Raios de borda" texto="Quanto maior a superfície, maior o raio: isso deixa a interface amigável sem parecer infantil.">
      <div className="ds-raios">{raios.map(([n, r]) => <div key={n} className="ds-raio"><div style={{ borderRadius: r }} /><b>{r}px</b><span>{n}</span></div>)}</div>
    </Secao>
    <Secao titulo="Elevação" texto="Uma única sombra suave (--sh) para cartões; sombras mais fortes só para o que flutua (hover de cartão, modal, toast).">
      <div className="ds-elevacoes">
        <div className="ds-elev" style={{ boxShadow: 'none', border: '1px solid var(--line)' }}>Plano<small>tabelas, campos</small></div>
        <div className="ds-elev" style={{ boxShadow: 'var(--sh)' }}>--sh<small>cartões</small></div>
        <div className="ds-elev" style={{ boxShadow: '0 12px 30px rgba(10,31,61,.14)' }}>Hover<small>cartão de carro</small></div>
        <div className="ds-elev" style={{ boxShadow: '0 24px 60px rgba(0,0,0,.35)' }}>Modal<small>sobre overlay</small></div>
      </div>
    </Secao>
    <Secao titulo="Grid e responsividade" texto="Conteúdo limitado a 1180px e centralizado. O catálogo usa grid automático (cartões de no mínimo 260px). Abaixo de 820px: formulários em uma coluna, menu rola na horizontal e ações dos pedidos descem para baixo do conteúdo." />
  </>
}

function Componentes() {
  const toast = useToast()
  const [modal, setModal] = useState(false)
  const [cpf, setCpf] = useState('')
  const [placa, setPlaca] = useState('')
  const [senha, setSenha] = useState('segredo1')
  return <>
    <Secao titulo="Botões" texto="Componente <Botao> ou classe .btn + variante. Use um primário por contexto; perigo só para ações destrutivas ou de recusa.">
      <Exemplo codigo={'<Botao>Solicitar aluguel</Botao>\n<Botao variante="teal">Aprovar</Botao>\n<Botao variante="sec">Alterar</Botao>\n<Botao variante="perigo">Cancelar pedido</Botao>\n<Botao variante="fantasma">Voltar</Botao>\n<Botao carregando>Salvando</Botao>'}>
        <Botao>Primário</Botao><Botao variante="teal">Teal (positivo)</Botao><Botao variante="sec">Secundário</Botao>
        <Botao variante="perigo">Perigo</Botao><Botao variante="fantasma">Fantasma</Botao><Botao carregando>Carregando</Botao><Botao disabled>Desabilitado</Botao>
      </Exemplo>
      <Exemplo rotulo="Tamanho pequeno (.sm) — usado em listas e tabelas">
        <Botao className="sm">Gerar contrato</Botao><Botao className="sm" variante="sec">Editar</Botao><Botao className="sm" variante="perigo">Excluir</Botao>
      </Exemplo>
    </Secao>
    <Secao titulo="Campos de formulário" texto="<Campo> envolve o input em um <label>, então clicar no rótulo foca o campo. Dicas ficam abaixo, em texto secundário. Máscaras formatam enquanto o usuário digita.">
      <Exemplo>
        <div className="form-grade" style={{ width: '100%' }}>
          <Campo rotulo="CPF" dica="Máscara 000.000.000-00"><input value={cpf} onChange={(e) => setCpf(mascaraCpf(e.target.value))} placeholder="000.000.000-00" inputMode="numeric" /></Campo>
          <Campo rotulo="Placa" dica="Padrão Mercosul, maiúsculas"><input value={placa} onChange={(e) => setPlaca(mascaraPlaca(e.target.value))} placeholder="ABC1D23" /></Campo>
          <CampoSenha rotulo="Senha" dica="Botão para mostrar/ocultar" value={senha} onChange={(e) => setSenha(e.target.value)} required={false} />
          <Campo rotulo="Tipo de contrato"><select><option>Propriedade da empresa</option><option>Propriedade do cliente</option></select></Campo>
        </div>
      </Exemplo>
    </Secao>
    <Secao titulo="Alertas e toasts" texto="Alertas ficam no topo do formulário ou da página e persistem até o usuário fechar ou corrigir. Toasts confirmam ações concluídas e somem sozinhos em 4 segundos.">
      <Exemplo>
        <div style={{ width: '100%' }}>
          <Alerta tipo="erro" onFechar={() => {}}>CPF inválido. Confira os dígitos.</Alerta>
          <Alerta tipo="info">Somente um agente bancário pode conceder contratos de crédito.</Alerta>
          <Alerta tipo="sucesso">Cadastro realizado com sucesso.</Alerta>
        </div>
      </Exemplo>
      <Exemplo codigo={"const toast = useToast()\ntoast('Pedido cancelado.')\ntoast('Falha ao salvar', 'erro')"}>
        <Botao variante="sec" onClick={() => toast('Pedido atualizado.')}>Mostrar toast de sucesso</Botao>
        <Botao variante="sec" onClick={() => toast('Não foi possível salvar.', 'erro')}>Mostrar toast de erro</Botao>
      </Exemplo>
    </Secao>
    <Secao titulo="Modal" texto="Usado para confirmações e formulários curtos sem tirar o usuário da lista. Fecha com Esc, com o × ou clicando fora; o fundo escurece e desfoca.">
      <Exemplo><Botao onClick={() => setModal(true)}>Abrir modal de exemplo</Botao></Exemplo>
      {modal && <Modal titulo="Cancelar pedido" onFechar={() => setModal(false)}>
        <p>Deseja cancelar o pedido <b>#42</b>? Esta ação não pode ser desfeita.</p>
        <div className="rodape-form"><button className="btn fantasma" onClick={() => setModal(false)}>Manter pedido</button><Botao variante="perigo" onClick={() => setModal(false)}>Cancelar pedido</Botao></div>
      </Modal>}
    </Secao>
    <Secao titulo="Cartão de automóvel" texto="A ilustração em SVG muda de cor conforme o modelo, diferenciando os carros sem depender de fotos.">
      <Exemplo>
        <div className="grade-cards" style={{ width: '100%' }}>
          {[['Fiat', 'Argo', 2024, 'ABC1D23'], ['VW', 'Polo', 2023, 'XYZ9K88'], ['Chevrolet', 'Onix', 2025, 'QWE4R56']].map(([m, mo, a, pl]) =>
            <article key={pl} className="cartao carro"><div className="carro-img"><CarroArte seed={mo + pl} /><em>{a}</em></div>
              <div className="carro-corpo"><h3>{m} {mo}</h3><span className="placa">{pl}</span><span className="meta">Matrícula MAT-00{a % 10}</span><Botao className="bloco sm">Solicitar aluguel</Botao></div></article>)}
        </div>
      </Exemplo>
    </Secao>
    <Secao titulo="Estados de carregamento e vazio" texto="Nenhuma lista aparece em branco: enquanto carrega, mostra um spinner; sem dados, uma mensagem que diz o que fazer em seguida.">
      <Exemplo><div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', width: '100%', gap: 12 }}>
        <div className="cartao"><Spinner /></div>
        <div className="cartao"><Vazio titulo="Nenhum pedido encontrado" texto="Escolha um carro no catálogo." /></div>
      </div></Exemplo>
    </Secao>
  </>
}

function Status() {
  const estados = [
    ['PENDENTE', 'Amarelo', 'Aguardando o agente. Cor de espera, sem alarme.'],
    ['EM_ANALISE', 'Azul', 'Análise financeira em andamento: a mesma cor da ação, indicando que algo está acontecendo.'],
    ['APROVADO', 'Verde', 'Parecer positivo; segue para o contrato.'],
    ['REPROVADO', 'Vermelho', 'Recusado, sempre acompanhado da justificativa.'],
    ['CANCELADO', 'Cinza', 'Encerrado pelo cliente; cor neutra, sem julgamento.'],
    ['EXECUTADO', 'Verde', 'Contrato de aluguel gerado.'],
  ]
  const exemplos = [['PENDENTE', 1, false], ['EM_ANALISE', 2, false], ['REPROVADO', 3, true], ['APROVADO', 4, false]]
  return <>
    <Secao titulo="Badges de estado" texto="A cor reforça o estado, mas o texto sempre aparece: quem não distingue cores entende do mesmo jeito.">
      <div className="ds-estados">{estados.map(([s, cor, d]) => <div key={s} className="ds-estado"><Badge valor={s} /><b>{cor}</b><p>{d}</p></div>)}</div>
    </Secao>
    <Secao titulo="Trilha do pedido" texto="Mostra as quatro etapas do fluxo do enunciado: solicitação → análise financeira → parecer → contrato. Etapas concluídas ficam em teal; uma recusa fica em vermelho.">
      {exemplos.map(([s, n, ruim]) => <div key={s} className="ds-trilha-ex">
        <Badge valor={s} />
        <div className="trilha">{[['Solicitado', 1], ['Em análise', 2], [ruim ? 'Recusado' : 'Aprovado', 3], ['Contrato', 4]].map(([r, i]) =>
          <div key={i} className={'passo ' + (i <= n ? (ruim && i === 3 ? 'ruim' : 'ok') : '')}>{r}</div>)}</div>
      </div>)}
    </Secao>
  </>
}

function Padroes() {
  const p = [
    ['Cabeçalho de página', 'Título (28px) + frase curta explicando a tela à esquerda; ações da página (busca, "+ Novo") à direita.', 'Todas as telas'],
    ['Lista de itens', 'Cartão com título + badge, linha de metadados, ações à direita e trilha embaixo. No celular, as ações descem para baixo do conteúdo.', 'Meus pedidos, Análise, Contratos'],
    ['Tabela de gestão', 'Para dados tabulares que o agente compara: cabeçalho em caixa alta pequena, linhas com divisória e ações no fim da linha.', 'Automóveis, Clientes'],
    ['Formulário em grade', 'Duas colunas no desktop e uma no celular; campos longos (endereço) ocupam a linha inteira. Botões alinhados à direita: secundário antes do primário.', 'Cadastro, Meus dados, modais'],
    ['Filtros por chips', 'Chips arredondados acima da lista; o ativo fica preenchido em navy. Filtram sem recarregar a página.', 'Pedidos'],
    ['Menu por perfil', 'Cliente e agente veem menus diferentes; telas fora do perfil não abrem nem digitando a URL.', 'Topo'],
    ['Confirmação de risco', 'Ações irreversíveis (cancelar pedido, excluir) abrem modal com o nome do item e o botão "perigo"; o botão seguro vem primeiro.', 'Cancelar, Excluir'],
  ]
  return <Secao titulo="Padrões de tela" texto="Combinações de componentes que se repetem. Seguir esses padrões faz qualquer tela nova parecer parte do mesmo produto.">
    <div className="cartao tabela-wrap"><table>
      <thead><tr><th>Padrão</th><th>Como funciona</th><th>Onde aparece</th></tr></thead>
      <tbody>{p.map(([n, d, o]) => <tr key={n}><td><b>{n}</b></td><td>{d}</td><td className="meta">{o}</td></tr>)}</tbody>
    </table></div>
  </Secao>
}

function Acessibilidade() {
  const nivel = (r) => r >= 7 ? ['AAA', 'b-aprovado'] : r >= 4.5 ? ['AA', 'b-aprovado'] : r >= 3 ? ['AA texto grande', 'b-pendente'] : ['Insuficiente', 'b-reprovado']
  return <>
    <Secao titulo="Contraste de cores (WCAG 2.1)" texto="Razões calculadas a partir dos tokens. Texto normal precisa de 4,5:1 (nível AA); textos grandes e ícones, de 3:1.">
      <TabelaContraste linhas={CONTRASTE} nivel={nivel} />
      <Alerta tipo="info">Pontos de atenção: o texto branco dos botões primário (3,9:1) e teal (2,5:1) fica abaixo de 4,5:1. Para chegar ao AA, os próximos passos seriam usar --blue-d (5,5:1) como cor sólida do primário e escurecer o teal dos botões.</Alerta>
    </Secao>
    <Secao titulo="Contraste no modo escuro" texto="Todas as combinações de texto do tema escuro atingem pelo menos AA.">
      <TabelaContraste linhas={CONTRASTE_ESCURO} nivel={nivel} />
    </Secao>
    <Secao titulo="Boas práticas aplicadas">
      <ul className="ds-lista">
        <li><b>Foco visível:</b> contorno azul de 3px em botões e campos (teal-claro no topo escuro) ao navegar pelo teclado.</li>
        <li><b>Rótulos reais:</b> todo campo está dentro de um &lt;label&gt;; botões só com ícone (×, Mostrar senha) têm aria-label.</li>
        <li><b>Modais:</b> role="dialog" e aria-modal, fecham com Esc.</li>
        <li><b>Alertas:</b> role="alert", então leitores de tela anunciam o erro na hora.</li>
        <li><b>Navegação:</b> o item atual do menu tem aria-current="page"; a tela fica na URL (#), então F5 e "voltar" funcionam.</li>
        <li><b>Movimento:</b> com "reduzir movimento" ativado no sistema, todas as animações são desligadas.</li>
        <li><b>Estado sem depender só de cor:</b> badges e trilha sempre têm texto.</li>
      </ul>
    </Secao>
  </>
}

function TabelaContraste({ linhas, nivel }) {
  return <div className="cartao tabela-wrap"><table>
    <thead><tr><th>Exemplo</th><th>Combinação</th><th>Razão</th><th>Nível</th></tr></thead>
    <tbody>{linhas.map(([n, fg, bg, r]) => { const [txt, cls] = nivel(r); return <tr key={n}>
      <td><span className="ds-contraste" style={{ color: fg, background: bg }}>Aa</span></td>
      <td>{n}<div className="meta">{fg} sobre {bg}</div></td>
      <td><b>{r.toFixed(1).replace('.', ',')}:1</b></td>
      <td><span className={'badge ' + cls}>{txt}</span></td></tr> })}</tbody>
  </table></div>
}
