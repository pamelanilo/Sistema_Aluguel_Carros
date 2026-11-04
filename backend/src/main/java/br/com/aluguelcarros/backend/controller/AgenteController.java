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
public class AgenteController {

    private final AgenteService agenteService;

    @org.springframework.beans.factory.annotation.Value("${app.setup-token:}")
    private String setupToken;

    // Criação de agentes: somente agente autenticado ou portador do token de instalação.
    private void autorizarCriacao(String token, org.springframework.security.core.Authentication auth) {
        boolean agente = auth != null && auth.isAuthenticated() && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENTE"));
        boolean tokenOk = setupToken != null && !setupToken.isBlank() && setupToken.equals(token);
        if (!agente && !tokenOk) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Criação de agentes exige agente autenticado ou X-Setup-Token válido.");
        }
    }

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    // Cadastrar agente empresarial
    @PostMapping("/empresas")
    public AgenteEmpresa cadastrarEmpresa(
            @RequestBody AgenteEmpresaRequest dados,
            @RequestHeader(value = "X-Setup-Token", required = false) String token,
            org.springframework.security.core.Authentication auth) {
        autorizarCriacao(token, auth);

        return agenteService.cadastrarEmpresa(dados);
    }

    // Cadastrar agente bancário
    @PostMapping("/bancos")
    public AgenteBanco cadastrarBanco(
            @RequestBody AgenteBancoRequest dados,
            @RequestHeader(value = "X-Setup-Token", required = false) String token,
            org.springframework.security.core.Authentication auth) {
        autorizarCriacao(token, auth);

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