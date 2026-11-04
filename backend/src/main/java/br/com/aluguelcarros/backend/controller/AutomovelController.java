
package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.dto.AutomovelRequest;
import br.com.aluguelcarros.backend.model.Automovel;
import br.com.aluguelcarros.backend.service.AutomovelService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/automoveis")
public class AutomovelController {

    private final AutomovelService automovelService;

    public AutomovelController(AutomovelService automovelService) {
        this.automovelService = automovelService;
    }

    // Cadastrar automóvel
    @PostMapping
    public Automovel cadastrar(@RequestBody AutomovelRequest dados) {
        return automovelService.cadastrar(dados);
    }

    // Listar automóveis
    @GetMapping
    public List<Automovel> listar() {
        return automovelService.listarTodos();
    }

    // Buscar automóvel pelo ID
    @GetMapping("/{id}")
    public Automovel buscar(@PathVariable Long id) {
        return automovelService.buscarPorId(id);
    }

    // Atualizar automóvel
    @PutMapping("/{id}")
    public Automovel atualizar(
            @PathVariable Long id,
            @RequestBody AutomovelRequest dados) {

        return automovelService.atualizar(id, dados);
    }

    // Excluir automóvel
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        automovelService.excluir(id);
    }
}
