package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.dto.AgenteEmpresaRequest;
import br.com.aluguelcarros.backend.dto.AgenteBancoRequest;

import br.com.aluguelcarros.backend.model.AgenteEmpresa;
import br.com.aluguelcarros.backend.model.AgenteBanco;
import br.com.aluguelcarros.backend.model.Usuario;

import br.com.aluguelcarros.backend.repository.UsuarioRepository;
import br.com.aluguelcarros.backend.repository.AgenteBancoRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgenteService {

    private final UsuarioRepository usuarioRepository;
    private final AgenteBancoRepository agenteBancoRepository;
    private final PasswordEncoder passwordEncoder;

    public AgenteService(
            UsuarioRepository usuarioRepository,
            AgenteBancoRepository agenteBancoRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.agenteBancoRepository = agenteBancoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Cadastrar agente empresarial
    public AgenteEmpresa cadastrarEmpresa(AgenteEmpresaRequest dados) {

        AgenteEmpresa agente = new AgenteEmpresa();

        agente.setNome(dados.getNome());
        agente.setLogin(dados.getLogin());
        agente.setSenha(passwordEncoder.encode(dados.getSenha()));
        agente.setCnpj(dados.getCnpj());
        agente.setRazaoSocial(dados.getRazaoSocial());

        return usuarioRepository.save(agente);
    }

    // Cadastrar agente bancário
    public AgenteBanco cadastrarBanco(AgenteBancoRequest dados) {

        AgenteBanco agente = new AgenteBanco();

        agente.setNome(dados.getNome());
        agente.setLogin(dados.getLogin());
        agente.setSenha(passwordEncoder.encode(dados.getSenha()));
        agente.setCnpj(dados.getCnpj());
        agente.setCodigoBanco(dados.getCodigoBanco());

        return usuarioRepository.save(agente);
    }

    // Listar agentes empresariais
    public List<AgenteEmpresa> listarEmpresas() {

        return usuarioRepository.findAll()
                .stream()
                .filter(usuario -> usuario instanceof AgenteEmpresa)
                .map(usuario -> (AgenteEmpresa) usuario)
                .toList();
    }

    // Listar agentes bancários
    public List<AgenteBanco> listarBancos() {

        return agenteBancoRepository.findAll();
    }
}