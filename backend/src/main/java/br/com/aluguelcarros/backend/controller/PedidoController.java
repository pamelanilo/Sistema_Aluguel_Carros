package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.PedidoRequest;
import br.com.aluguelcarros.backend.model.Pedido;
import br.com.aluguelcarros.backend.service.PedidoService;
import org.springframework.web.bind.annotation.*;
import br.com.aluguelcarros.backend.dto.AvaliacaoRequest;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public Pedido criar(@RequestBody PedidoRequest dados) {
        return pedidoService.criar(dados);
    }

    @GetMapping
    public List<Pedido> listar() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Pedido buscar(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
public Pedido atualizar(
        @PathVariable Long id,
        @RequestBody PedidoRequest dados) {

    return pedidoService.atualizar(id, dados);
}

@PatchMapping("/{id}/cancelar")
public Pedido cancelar(@PathVariable Long id) {
    return pedidoService.cancelar(id);
}

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
