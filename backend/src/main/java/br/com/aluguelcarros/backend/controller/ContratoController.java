package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.ContratoAluguelRequest;
import br.com.aluguelcarros.backend.dto.ContratoCreditoRequest;
import br.com.aluguelcarros.backend.dto.ContratoAluguelResponse;
import br.com.aluguelcarros.backend.dto.ContratoCreditoResponse;

import br.com.aluguelcarros.backend.model.ContratoAluguel;
import br.com.aluguelcarros.backend.model.ContratoCredito;
import br.com.aluguelcarros.backend.service.ContratoService;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @PostMapping("/aluguel")
    public Map<String, Object> criarContratoAluguel(
            @RequestBody ContratoAluguelRequest dados,
            Authentication authentication) {

        ContratoAluguel contrato =
                contratoService.criarContratoAluguel(dados, authentication.getName());

        return Map.of(
                "mensagem", "Contrato de aluguel criado com sucesso",
                "id", contrato.getId()
        );
    }

    @PostMapping("/credito")
    public Map<String, Object> criarContratoCredito(
            @RequestBody ContratoCreditoRequest dados,
            Authentication authentication) {

        ContratoCredito credito =
                contratoService.criarContratoCredito(dados, authentication.getName());

        return Map.of(
                "mensagem", "Contrato de crédito criado com sucesso",
                "id", credito.getId()
        );
    }

    private boolean ehAgente(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENTE"));
    }

    // Agente vê todos os contratos; cliente vê somente os dos seus pedidos.
    @GetMapping("/aluguel")
    public List<ContratoAluguelResponse> listarContratosAluguel(Authentication authentication) {
        return ehAgente(authentication)
                ? contratoService.listarContratosAluguel()
                : contratoService.listarContratosAluguelDoCliente(authentication.getName());
    }

    @GetMapping("/aluguel/{id}")
    public ContratoAluguelResponse buscarContratoAluguel(
            @PathVariable Long id) {
        return contratoService.buscarContratoAluguel(id);
    }

    @GetMapping("/credito")
    public List<ContratoCreditoResponse> listarContratosCredito(Authentication authentication) {
        return ehAgente(authentication)
                ? contratoService.listarContratosCredito()
                : contratoService.listarContratosCreditoDoCliente(authentication.getName());
    }

    @GetMapping("/credito/{id}")
    public ContratoCreditoResponse buscarContratoCredito(
            @PathVariable Long id) {
        return contratoService.buscarContratoCredito(id);
    }
}