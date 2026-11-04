package br.com.aluguelcarros.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {
    @org.springframework.beans.factory.annotation.Value("${app.cors.origins:http://localhost:5173}")
    private String corsOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/clientes").permitAll()
                .requestMatchers(HttpMethod.POST, "/agentes/empresas", "/agentes/bancos").permitAll() // protegido por X-Setup-Token no controller
                .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                .requestMatchers(HttpMethod.POST, "/pedidos").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.PATCH, "/pedidos/*/cancelar").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.PATCH, "/pedidos/*/avaliar", "/pedidos/*/analisar").hasRole("AGENTE")
                .requestMatchers(HttpMethod.PUT, "/pedidos/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/contratos/aluguel", "/contratos/credito").authenticated() // cliente vê só os próprios (filtrado no controller)
                .requestMatchers("/contratos/**").hasRole("AGENTE")
                .requestMatchers("/agentes/**").hasRole("AGENTE")
                .requestMatchers(HttpMethod.POST, "/automoveis/**").hasRole("AGENTE")
                .requestMatchers(HttpMethod.PUT, "/automoveis/**").hasRole("AGENTE")
                .requestMatchers(HttpMethod.DELETE, "/automoveis/**").hasRole("AGENTE")
                .requestMatchers("/clientes", "/clientes/**").hasRole("AGENTE")
                .anyRequest().authenticated())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(corsOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
