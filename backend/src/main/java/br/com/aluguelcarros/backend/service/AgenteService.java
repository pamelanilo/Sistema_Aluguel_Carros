
package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.dto.AgenteEmpresaRequest;
import br.com.aluguelcarros.backend.dto.AgenteBancoRequest;
import br.com.aluguelcarros.backend.model.AgenteEmpresa;
import br.com.aluguelcarros.backend.model.AgenteBanco;
import br.com.aluguelcarros.backend.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AgenteService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AgenteService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
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
}
