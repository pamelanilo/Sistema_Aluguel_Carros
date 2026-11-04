package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.AvaliacaoRequest;
import br.com.aluguelcarros.backend.dto.PedidoRequest;
import br.com.aluguelcarros.backend.dto.PedidoResponse;
import br.com.aluguelcarros.backend.model.enums.StatusPedido;
import br.com.aluguelcarros.backend.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;
    public PedidoController(PedidoService pedidoService) { this.pedidoService = pedidoService; }

    private boolean ehAgente(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AGENTE"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@RequestBody PedidoRequest dados, Authentication auth) {
        return pedidoService.criar(dados, auth.getName());
    }

    @GetMapping
    public List<PedidoResponse> listar(Authentication auth) {
        return ehAgente(auth) ? pedidoService.listarTodos() : pedidoService.listarDoCliente(auth.getName());
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, Authentication auth) {
        return pedidoService.buscarPorId(id, auth.getName(), ehAgente(auth));
    }

    @PutMapping("/{id}")
    public PedidoResponse atualizar(@PathVariable Long id, @RequestBody PedidoRequest dados, Authentication auth) {
        return pedidoService.atualizar(id, dados, auth.getName(), ehAgente(auth));
    }

    @PatchMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, Authentication auth) {
        return pedidoService.cancelar(id, auth.getName());
    }

    @PatchMapping("/{id}/analisar")
    public PedidoResponse analisar(@PathVariable Long id, Authentication auth) {
        if (!ehAgente(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas agentes podem analisar pedidos.");
        return pedidoService.iniciarAnalise(id, auth.getName());
    }

    @PatchMapping("/{id}/avaliar")
    public PedidoResponse avaliar(@PathVariable Long id, @RequestBody AvaliacaoRequest dados, Authentication auth) {
        if (!ehAgente(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas agentes podem avaliar pedidos.");
        if (dados.getParecer() == null || dados.getParecer().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o parecer da análise.");
        return pedidoService.avaliar(id, dados.getParecer(), dados.getStatus(), auth.getName());
    }
}
