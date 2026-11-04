package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.ContratoAluguelRequest;
import br.com.aluguelcarros.backend.dto.ContratoCreditoRequest;
import br.com.aluguelcarros.backend.dto.ContratoAluguelResponse;
import br.com.aluguelcarros.backend.dto.ContratoCreditoResponse;

import br.com.aluguelcarros.backend.model.ContratoAluguel;
import br.com.aluguelcarros.backend.model.ContratoCredito;
import br.com.aluguelcarros.backend.service.ContratoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contratos")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @PostMapping("/aluguel")
    public Map<String, Object> criarContratoAluguel(
            @RequestBody ContratoAluguelRequest dados) {

        ContratoAluguel contrato =
                contratoService.criarContratoAluguel(dados);

        return Map.of(
                "mensagem", "Contrato de aluguel criado com sucesso",
                "id", contrato.getId()
        );
    }

    @PostMapping("/credito")
    public Map<String, Object> criarContratoCredito(
            @RequestBody ContratoCreditoRequest dados) {

        ContratoCredito credito =
                contratoService.criarContratoCredito(dados);

        return Map.of(
                "mensagem", "Contrato de crédito criado com sucesso",
                "id", credito.getId()
        );
    }

    @GetMapping("/aluguel")
    public List<ContratoAluguelResponse> listarContratosAluguel() {
        return contratoService.listarContratosAluguel();
    }

    @GetMapping("/aluguel/{id}")
    public ContratoAluguelResponse buscarContratoAluguel(
            @PathVariable Long id) {
        return contratoService.buscarContratoAluguel(id);
    }

    @GetMapping("/credito")
    public List<ContratoCreditoResponse> listarContratosCredito() {
        return contratoService.listarContratosCredito();
    }

    @GetMapping("/credito/{id}")
    public ContratoCreditoResponse buscarContratoCredito(
            @PathVariable Long id) {
        return contratoService.buscarContratoCredito(id);
    }
}