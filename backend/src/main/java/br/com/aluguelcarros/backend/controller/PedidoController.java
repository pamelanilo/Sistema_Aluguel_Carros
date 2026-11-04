package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.PedidoRequest;
import br.com.aluguelcarros.backend.dto.PedidoResponse;
import br.com.aluguelcarros.backend.dto.AvaliacaoRequest;
import br.com.aluguelcarros.backend.service.PedidoService;

import org.springframework.web.bind.annotation.*;

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
    public PedidoResponse criar(@RequestBody PedidoRequest dados) {
        return pedidoService.criar(dados);
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public PedidoResponse atualizar(
            @PathVariable Long id,
            @RequestBody PedidoRequest dados) {

        return pedidoService.atualizar(id, dados);
    }

    @PatchMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id) {
        return pedidoService.cancelar(id);
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