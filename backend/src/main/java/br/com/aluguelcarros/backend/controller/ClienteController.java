package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.model.Cliente;
import br.com.aluguelcarros.backend.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ---- Cliente autenticado: consulta e alteração dos próprios dados ----

    @GetMapping("/me")
    public Cliente meusDados(Authentication auth) {
        return clienteService.buscarPorLogin(auth.getName());
    }

    @PutMapping("/me")
    public Cliente atualizarMeusDados(@RequestBody Cliente dados, Authentication auth) {
        Cliente atual = clienteService.buscarPorLogin(auth.getName());
        return clienteService.atualizar(atual.getId(), dados);
    }

    // ---- Agentes: CRUD completo de clientes ----

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    // Público: auto-cadastro do cliente (também usado pelo agente).
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente cadastrar(@RequestBody Cliente cliente) {
        return clienteService.cadastrar(cliente);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(
            @PathVariable Long id,
            @RequestBody Cliente cliente) {
        return clienteService.atualizar(id, cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        clienteService.excluir(id);
    }
}
