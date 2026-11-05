package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.AgenteBancoRequest;
import br.com.aluguelcarros.backend.dto.AgenteEmpresaRequest;
import br.com.aluguelcarros.backend.model.AgenteBanco;
import br.com.aluguelcarros.backend.model.AgenteEmpresa;
import br.com.aluguelcarros.backend.service.AgenteService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/agentes")
public class AgenteController {

    private final AgenteService agenteService;

    @Value("${app.setup-token:}")
    private String setupToken;

    @Value("${app.cadastro-agentes-publico:true}")
    private boolean cadastroPublico;

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    /** Informa ao front-end se a tela de cadastro pode oferecer Empresa e Banco. */
    @GetMapping("/cadastro-publico")
    public Map<String, Boolean> cadastroPublico() {
        return Map.of("habilitado", cadastroPublico);
    }

    // Criação de agentes: liberada se o autocadastro público estiver ativo;
    // caso contrário, exige agente autenticado ou o token de instalação.
    private void autorizarCriacao(String token, Authentication auth) {
        if (cadastroPublico) {
            return;
        }
        boolean agente = auth != null && auth.isAuthenticated() && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENTE"));
        boolean tokenOk = setupToken != null && !setupToken.isBlank() && setupToken.equals(token);
        if (!agente && !tokenOk) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "O cadastro de empresas e bancos está restrito. Solicite a um agente ou use o X-Setup-Token.");
        }
    }

    @PostMapping("/empresas")
    @ResponseStatus(HttpStatus.CREATED)
    public AgenteEmpresa cadastrarEmpresa(
            @RequestBody AgenteEmpresaRequest dados,
            @RequestHeader(value = "X-Setup-Token", required = false) String token,
            Authentication auth) {
        autorizarCriacao(token, auth);
        return agenteService.cadastrarEmpresa(dados);
    }

    @PostMapping("/bancos")
    @ResponseStatus(HttpStatus.CREATED)
    public AgenteBanco cadastrarBanco(
            @RequestBody AgenteBancoRequest dados,
            @RequestHeader(value = "X-Setup-Token", required = false) String token,
            Authentication auth) {
        autorizarCriacao(token, auth);
        return agenteService.cadastrarBanco(dados);
    }

    @GetMapping("/empresas")
    public List<AgenteEmpresa> listarEmpresas() {
        return agenteService.listarEmpresas();
    }

    @GetMapping("/bancos")
    public List<AgenteBanco> listarBancos() {
        return agenteService.listarBancos();
    }
}
