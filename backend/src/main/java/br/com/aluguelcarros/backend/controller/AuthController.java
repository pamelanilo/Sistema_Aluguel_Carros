package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.model.Usuario;
import br.com.aluguelcarros.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody(required = false) Map<String, String> dados,
            HttpServletRequest request) {

        String login = dados == null ? null : dados.get("login");
        String senha = dados == null ? null : dados.get("senha");

        Usuario usuario = authService.autenticar(login, senha).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Login ou senha incorretos"));
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario.getLogin(), null,
                List.of(new SimpleGrantedAuthority(authService.perfil(usuario))));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Evita fixação de sessão: descarta a sessão anterior antes de criar a autenticada.
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return ResponseEntity.ok(dadosUsuario(usuario));
    }

    @GetMapping("/me")
    public ResponseEntity<?> usuarioLogado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Usuário não autenticado"));
        }

        return authService.buscarPorLogin(authentication.getName())
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(dadosUsuario(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensagem", "Usuário não encontrado")));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return ResponseEntity.ok(Map.of("mensagem", "Logout realizado com sucesso"));
    }

    private Map<String, Object> dadosUsuario(Usuario usuario) {
        return Map.of(
                "id", usuario.getId(),
                "nome", usuario.getNome(),
                "login", usuario.getLogin(),
                "tipo", usuario.getClass().getSimpleName());
    }
}
