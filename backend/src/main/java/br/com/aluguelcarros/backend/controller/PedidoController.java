
package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.PedidoRequest;
import br.com.aluguelcarros.backend.dto.AvaliacaoRequest;
import br.com.aluguelcarros.backend.model.Pedido;
import br.com.aluguelcarros.backend.service.PedidoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Criar pedido
    @PostMapping
    public Pedido criar(@RequestBody PedidoRequest dados) {
        return pedidoService.criar(dados);
    }

    // Listar pedidos
    @GetMapping
    public List<Pedido> listar() {
        return pedidoService.listarTodos();
    }

    // Buscar pedido por ID
    @GetMapping("/{id}")
    public Pedido buscar(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    // Atualizar pedido
    @PutMapping("/{id}")
    public Pedido atualizar(
            @PathVariable Long id,
            @RequestBody PedidoRequest dados) {

        return pedidoService.atualizar(id, dados);
    }

    // Cancelar pedido
    @PatchMapping("/{id}/cancelar")
    public Pedido cancelar(@PathVariable Long id) {
        return pedidoService.cancelar(id);
    }

    // Avaliar pedido
    @PatchMapping("/{id}/avaliar")
    public Pedido avaliar(
            @PathVariable Long id,
            @RequestBody AvaliacaoRequest dados) {

        return pedidoService.avaliar(
                id,
                dados.getParecer(),
                dados.getStatus()
        );
    }
}
