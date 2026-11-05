# Versões dos modelos UML

| Versão | Origem | O que mudou |
|---|---|---|
| [v0](v0/) | Commit `bfdf8db` (modelagem inicial) | Casos de uso, classes e pacotes da primeira entrega |
| [v1](v1/) | Commit `e12d254` ("mudanças finais") | Casos de uso reorganizados, com análise financeira em duas etapas (iniciar → aprovar/recusar com justificativa), geração e consulta de contratos (cliente vê só os seus) e manutenção de automóveis; enum `StatusContrato` no diagrama de classes; camadas MVC e pacote `exception` no diagrama de pacotes |
| v2 (atual, em `docs/diagramas/`) | Revisão final | Meus dados do cliente, consulta de renda na análise financeira, cadastro de empresa/banco, regra de disponibilidade do automóvel, diagrama de classes em três visões (model · camadas · dto/exception), subsistemas do enunciado e `AcessoNegadoException` |

## Como gerar os PNGs
```bash
java -jar plantuml.jar -tpng -charset UTF-8 docs/diagramas/*.puml
```
Com Graphviz instalado o PlantUML usa o layout padrão. Sem Graphviz, os arquivos com `!pragma layout smetana` usam o layout embutido. O diagrama de casos de uso foi gerado em SVG com o layout embutido e convertido para PNG após ajustar a área visível (o layout embutido posiciona o título fora da área).
