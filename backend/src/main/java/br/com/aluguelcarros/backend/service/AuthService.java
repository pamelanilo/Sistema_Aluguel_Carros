package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.model.Cliente;
import br.com.aluguelcarros.backend.model.Usuario;
import br.com.aluguelcarros.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    public static final String ROLE_CLIENTE = "ROLE_CLIENTE";
    public static final String ROLE_AGENTE = "ROLE_AGENTE";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Retorna o usuário quando login e senha conferem; vazio caso contrário. */
    public Optional<Usuario> autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }
        return usuarioRepository.findByLogin(login.trim())
                .filter(usuario -> passwordEncoder.matches(senha, usuario.getSenha()));
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    /** Clientes recebem ROLE_CLIENTE; agentes de empresa e de banco recebem ROLE_AGENTE. */
    public String perfil(Usuario usuario) {
        return usuario instanceof Cliente ? ROLE_CLIENTE : ROLE_AGENTE;
    }
}
