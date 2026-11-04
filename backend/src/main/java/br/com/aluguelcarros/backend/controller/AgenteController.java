
package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.AgenteEmpresaRequest;
import br.com.aluguelcarros.backend.dto.AgenteBancoRequest;
import br.com.aluguelcarros.backend.model.AgenteEmpresa;
import br.com.aluguelcarros.backend.model.AgenteBanco;
import br.com.aluguelcarros.backend.service.AgenteService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agentes")
@CrossOrigin(origins = "*")
public class AgenteController {

    private final AgenteService agenteService;

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    // Cadastrar agente empresarial
    @PostMapping("/empresas")
    public AgenteEmpresa cadastrarEmpresa(
            @RequestBody AgenteEmpresaRequest dados) {

        return agenteService.cadastrarEmpresa(dados);
    }

    // Cadastrar agente bancário
    @PostMapping("/bancos")
    public AgenteBanco cadastrarBanco(
            @RequestBody AgenteBancoRequest dados) {

        return agenteService.cadastrarBanco(dados);
    }
}
