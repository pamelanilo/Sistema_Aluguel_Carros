package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.dto.AgenteBancoRequest;
import br.com.aluguelcarros.backend.dto.AgenteEmpresaRequest;
import br.com.aluguelcarros.backend.exception.RegraNegocioException;
import br.com.aluguelcarros.backend.model.Agente;
import br.com.aluguelcarros.backend.model.AgenteBanco;
import br.com.aluguelcarros.backend.model.AgenteEmpresa;
import br.com.aluguelcarros.backend.repository.AgenteBancoRepository;
import br.com.aluguelcarros.backend.repository.AgenteEmpresaRepository;
import br.com.aluguelcarros.backend.repository.AgenteRepository;
import br.com.aluguelcarros.backend.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AgenteService {

    private final UsuarioRepository usuarioRepository;
    private final AgenteRepository agenteRepository;
    private final AgenteEmpresaRepository agenteEmpresaRepository;
    private final AgenteBancoRepository agenteBancoRepository;
    private final PasswordEncoder passwordEncoder;

    public AgenteService(
            UsuarioRepository usuarioRepository,
            AgenteRepository agenteRepository,
            AgenteEmpresaRepository agenteEmpresaRepository,
            AgenteBancoRepository agenteBancoRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.agenteRepository = agenteRepository;
        this.agenteEmpresaRepository = agenteEmpresaRepository;
        this.agenteBancoRepository = agenteBancoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Cadastrar agente empresarial
    public AgenteEmpresa cadastrarEmpresa(AgenteEmpresaRequest dados) {
        if (dados == null || vazio(dados.getRazaoSocial())) {
            throw new RegraNegocioException("Informe a razão social da empresa.");
        }
        AgenteEmpresa agente = new AgenteEmpresa();
        preencherComuns(agente, dados.getNome(), dados.getLogin(), dados.getSenha(), dados.getCnpj());
        agente.setRazaoSocial(dados.getRazaoSocial().trim());
        return agenteEmpresaRepository.save(agente);
    }

    // Cadastrar agente bancário
    public AgenteBanco cadastrarBanco(AgenteBancoRequest dados) {
        String codigo = dados == null || dados.getCodigoBanco() == null ? "" : dados.getCodigoBanco().replaceAll("\\D", "");
        if (codigo.length() != 3) {
            throw new RegraNegocioException("Informe o código do banco com 3 dígitos (ex.: 001, 341).");
        }
        AgenteBanco agente = new AgenteBanco();
        preencherComuns(agente, dados.getNome(), dados.getLogin(), dados.getSenha(), dados.getCnpj());
        agente.setCodigoBanco(codigo);
        return agenteBancoRepository.save(agente);
    }

    @Transactional(readOnly = true)
    public List<AgenteEmpresa> listarEmpresas() {
        return agenteEmpresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AgenteBanco> listarBancos() {
        return agenteBancoRepository.findAll();
    }

    private void preencherComuns(Agente agente, String nome, String login, String senha, String cnpj) {
        if (vazio(nome)) {
            throw new RegraNegocioException("Informe o nome do responsável.");
        }
        if (vazio(login) || usuarioRepository.existsByLogin(login.trim())) {
            throw new RegraNegocioException("Login inválido ou já utilizado.");
        }
        if (senha == null || senha.length() < 6) {
            throw new RegraNegocioException("A senha deve ter ao menos 6 caracteres.");
        }
        String cnpjLimpo = validarCnpj(cnpj);
        if (agenteRepository.existsByCnpj(cnpjLimpo)) {
            throw new RegraNegocioException("Já existe um agente cadastrado com este CNPJ.");
        }
        agente.setNome(nome.trim());
        agente.setLogin(login.trim());
        agente.setSenha(passwordEncoder.encode(senha));
        agente.setCnpj(cnpjLimpo);
    }

    // Normaliza (somente dígitos) e valida os dígitos verificadores do CNPJ.
    static String validarCnpj(String cnpj) {
        String d = cnpj == null ? "" : cnpj.replaceAll("\\D", "");
        if (d.length() != 14 || d.chars().distinct().count() == 1) {
            throw new RegraNegocioException("CNPJ inválido.");
        }
        int[] pesos = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int t = 12; t < 14; t++) {
            int soma = 0;
            for (int i = 0; i < t; i++) {
                soma += (d.charAt(i) - '0') * pesos[i + 13 - t];
            }
            int resto = soma % 11;
            int dv = resto < 2 ? 0 : 11 - resto;
            if (dv != d.charAt(t) - '0') {
                throw new RegraNegocioException("CNPJ inválido.");
            }
        }
        return d;
    }

    private static boolean vazio(String s) {
        return s == null || s.isBlank();
    }
}
