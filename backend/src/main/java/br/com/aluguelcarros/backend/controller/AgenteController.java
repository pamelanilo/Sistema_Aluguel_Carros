package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.AgenteEmpresaRequest;
import br.com.aluguelcarros.backend.dto.AgenteBancoRequest;

import br.com.aluguelcarros.backend.model.AgenteEmpresa;
import br.com.aluguelcarros.backend.model.AgenteBanco;

import br.com.aluguelcarros.backend.service.AgenteService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agentes")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
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

    // Listar agentes empresariais
    @GetMapping("/empresas")
    public List<AgenteEmpresa> listarEmpresas() {

        return agenteService.listarEmpresas();
    }

    // Listar agentes bancários
    @GetMapping("/bancos")
    public List<AgenteBanco> listarBancos() {

        return agenteService.listarBancos();
    }
}