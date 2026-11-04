package br.com.aluguelcarros.backend.controller;

import br.com.aluguelcarros.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Map<String, String> dados) {

        String login = dados.get("login");
        String senha = dados.get("senha");

        boolean autenticado = authService.autenticar(login, senha);

        if (autenticado) {
            return ResponseEntity.ok(
                    Map.of("mensagem", "Login realizado com sucesso")
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensagem", "Login ou senha incorretos"));
    }
}