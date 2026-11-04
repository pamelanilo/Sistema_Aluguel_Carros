
package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.ContratoAluguelRequest;
import br.com.aluguelcarros.backend.dto.ContratoCreditoRequest;
import br.com.aluguelcarros.backend.model.ContratoAluguel;
import br.com.aluguelcarros.backend.model.ContratoCredito;
import br.com.aluguelcarros.backend.service.ContratoService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/contratos")
@CrossOrigin(origins = "*")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    // Criar contrato de aluguel
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

    // Criar contrato de crédito
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
}
