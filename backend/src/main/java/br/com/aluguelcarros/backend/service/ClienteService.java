package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.model.Cliente;
import br.com.aluguelcarros.backend.model.Rendimento;
import br.com.aluguelcarros.backend.repository.ClienteRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        validarRendimentos(cliente.getRendimentos());

        if (cliente.getSenha() == null || cliente.getSenha().isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }

        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));

        if (cliente.getRendimentos() != null) {
            for (Rendimento rendimento : cliente.getRendimentos()) {
                rendimento.setCliente(cliente);
            }
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente dados) {
        Cliente cliente = buscarPorId(id);

        validarRendimentos(dados.getRendimentos());

        cliente.setNome(dados.getNome());
        cliente.setLogin(dados.getLogin());
        cliente.setRg(dados.getRg());
        cliente.setCpf(dados.getCpf());
        cliente.setEndereco(dados.getEndereco());
        cliente.setProfissao(dados.getProfissao());

        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            cliente.setSenha(passwordEncoder.encode(dados.getSenha()));
        }

        // Na atualização, a lista enviada substitui a lista atual.
        if (dados.getRendimentos() != null) {
            cliente.getRendimentos().clear();

            for (Rendimento rendimentoDados : dados.getRendimentos()) {
                Rendimento rendimento = new Rendimento();
                rendimento.setEntidadeEmpregadora(
                        rendimentoDados.getEntidadeEmpregadora());
                rendimento.setValor(rendimentoDados.getValor());
                cliente.adicionarRendimento(rendimento);
            }
        }

        return clienteRepository.save(cliente);
    }

    private void validarRendimentos(List<Rendimento> rendimentos) {
        if (rendimentos == null) {
            return;
        }

        if (rendimentos.size() > 3) {
            throw new IllegalArgumentException(
                    "O cliente pode possuir no máximo 3 rendimentos.");
        }

        for (Rendimento rendimento : rendimentos) {
            if (rendimento == null
                    || rendimento.getEntidadeEmpregadora() == null
                    || rendimento.getEntidadeEmpregadora().isBlank()
                    || rendimento.getValor() == null
                    || rendimento.getValor().signum() <= 0) {
                throw new IllegalArgumentException(
                        "Informe a entidade empregadora e um valor de rendimento maior que zero.");
            }
        }
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }
}
