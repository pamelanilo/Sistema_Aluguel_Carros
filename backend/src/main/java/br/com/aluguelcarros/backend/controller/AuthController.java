package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.model.Usuario;
import br.com.aluguelcarros.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Map<String, String> dados,
            HttpServletRequest request,
            HttpServletResponse response) {

        String login = dados.get("login");
        String senha = dados.get("senha");

        Usuario usuario = usuarioRepository.findByLogin(login).orElse(null);

        if (usuario == null || !passwordEncoder.matches(senha, usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Login ou senha incorretos"));
        }

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        usuario.getLogin(),
                        null,
                        Collections.emptyList()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return ResponseEntity.ok(
                Map.of("mensagem", "Login realizado com sucesso")
        );
    }

    @GetMapping("/me")
        public ResponseEntity<?> usuarioLogado(Authentication authentication) {

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("mensagem", "Usuário não autenticado"));
            }

            Usuario usuario = usuarioRepository
                    .findByLogin(authentication.getName())
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensagem", "Usuário não encontrado"));
            }

            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "nome", usuario.getNome(),
                    "login", usuario.getLogin(),
                    "tipo", usuario.getClass().getSimpleName()
            ));
        }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request) {

        request.getSession(false);
        SecurityContextHolder.clearContext();

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        return ResponseEntity.ok(
                Map.of("mensagem", "Logout realizado com sucesso")
        );
    }
}