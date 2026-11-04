package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.model.Cliente;
import br.com.aluguelcarros.backend.repository.ClienteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Cliente cadastrar(Cliente cliente) {
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente dados) {
        Cliente cliente = buscarPorId(id);

        cliente.setNome(dados.getNome());
        cliente.setLogin(dados.getLogin());
        cliente.setRg(dados.getRg());
        cliente.setCpf(dados.getCpf());
        cliente.setEndereco(dados.getEndereco());
        cliente.setProfissao(dados.getProfissao());

        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            cliente.setSenha(passwordEncoder.encode(dados.getSenha()));
        }

        return clienteRepository.save(cliente);
    }

    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }
}