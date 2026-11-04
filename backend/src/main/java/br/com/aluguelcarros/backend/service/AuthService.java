package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.model.Usuario;
import br.com.aluguelcarros.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean autenticar(String login, String senha) {

        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElse(null);

        if (usuario == null) {
            return false;
        }

        return passwordEncoder.matches(senha, usuario.getSenha());
    }
}