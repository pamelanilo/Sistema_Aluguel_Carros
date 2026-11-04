package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.PedidoRequest;
import br.com.aluguelcarros.backend.dto.PedidoResponse;
import br.com.aluguelcarros.backend.dto.AvaliacaoRequest;
import br.com.aluguelcarros.backend.service.PedidoService;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public PedidoResponse criar(@RequestBody PedidoRequest dados, Authentication authentication) {
        return pedidoService.criar(dados, authentication.getName());
    }

    @GetMapping
    public List<PedidoResponse> listar(Authentication authentication) {
        boolean agente = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENTE"));
        return agente ? pedidoService.listarTodos()
                : pedidoService.listarDoCliente(authentication.getName());
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, Authentication authentication) {
        boolean agente = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENTE"));
        return pedidoService.buscarPorId(id, authentication.getName(), agente);
    }

    @PutMapping("/{id}")
    public PedidoResponse atualizar(
            @PathVariable Long id,
            @RequestBody PedidoRequest dados) {

        return pedidoService.atualizar(id, dados);
    }

    @PatchMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, Authentication authentication) {
        return pedidoService.cancelar(id, authentication.getName());
    }

    @PatchMapping("/{id}/avaliar")
    public PedidoResponse avaliar(
            @PathVariable Long id,
            @RequestBody AvaliacaoRequest dados) {

        return pedidoService.avaliar(
                id,
                dados.getParecer(),
                dados.getStatus()
        );
    }
}