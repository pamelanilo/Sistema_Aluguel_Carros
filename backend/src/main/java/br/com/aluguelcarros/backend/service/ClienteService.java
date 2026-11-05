package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.exception.RecursoNaoEncontradoException;
import br.com.aluguelcarros.backend.exception.RegraNegocioException;

import br.com.aluguelcarros.backend.model.Cliente;
import br.com.aluguelcarros.backend.model.Rendimento;
import br.com.aluguelcarros.backend.repository.ClienteRepository;
import br.com.aluguelcarros.backend.repository.PedidoRepository;
import br.com.aluguelcarros.backend.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorLogin(String login) {
        return clienteRepository.findByLogin(login)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        validarRendimentos(cliente.getRendimentos());
        cliente.setCpf(validarCpf(cliente.getCpf()));
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new RegraNegocioException("Já existe um cliente cadastrado com este CPF.");
        }
        if (cliente.getLogin() == null || cliente.getLogin().isBlank()
                || usuarioRepository.existsByLogin(cliente.getLogin())) {
            throw new RegraNegocioException("Login inválido ou já utilizado.");
        }

        if (cliente.getSenha() == null || cliente.getSenha().isBlank()) {
            throw new RegraNegocioException("A senha é obrigatória.");
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
        dados.setCpf(validarCpf(dados.getCpf()));
        if (clienteRepository.existsByCpfAndIdNot(dados.getCpf(), id)) {
            throw new RegraNegocioException("Já existe outro cliente com este CPF.");
        }
        if (dados.getLogin() == null || dados.getLogin().isBlank()
                || usuarioRepository.existsByLoginAndIdNot(dados.getLogin(), id)) {
            throw new RegraNegocioException("Login inválido ou já utilizado por outro usuário.");
        }

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

    // Normaliza (somente dígitos) e valida os dígitos verificadores do CPF.
    private String validarCpf(String cpf) {
        String d = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (d.length() != 11 || d.chars().distinct().count() == 1) {
            throw new RegraNegocioException("CPF inválido.");
        }
        for (int t = 9; t < 11; t++) {
            int soma = 0;
            for (int i = 0; i < t; i++) {
                soma += (d.charAt(i) - '0') * (t + 1 - i);
            }
            int dv = (soma * 10) % 11 % 10;
            if (dv != d.charAt(t) - '0') {
                throw new RegraNegocioException("CPF inválido.");
            }
        }
        return d;
    }

    private void validarRendimentos(List<Rendimento> rendimentos) {
        if (rendimentos == null) {
            return;
        }

        if (rendimentos.size() > 3) {
            throw new RegraNegocioException(
                    "O cliente pode possuir no máximo 3 rendimentos.");
        }

        for (Rendimento rendimento : rendimentos) {
            if (rendimento == null
                    || rendimento.getEntidadeEmpregadora() == null
                    || rendimento.getEntidadeEmpregadora().isBlank()
                    || rendimento.getValor() == null
                    || rendimento.getValor().signum() <= 0) {
                throw new RegraNegocioException(
                        "Informe a entidade empregadora e um valor de rendimento maior que zero.");
            }
        }
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        if (pedidoRepository.existsByCliente_Id(id)) {
            throw new RegraNegocioException(
                    "Este cliente possui pedidos registrados e não pode ser excluído.");
        }
        clienteRepository.delete(cliente);
    }
}
