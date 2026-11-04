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

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/clientes", "/agentes/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/clientes/**").hasRole("AGENTE")
                .requestMatchers(HttpMethod.PUT, "/clientes/**").hasRole("AGENTE")
                .requestMatchers(HttpMethod.POST, "/pedidos").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.PATCH, "/pedidos/*/cancelar").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.PATCH, "/pedidos/*/avaliar").hasRole("AGENTE")
                .requestMatchers(HttpMethod.POST, "/automoveis/**", "/agentes/**").hasRole("AGENTE")
                .requestMatchers(HttpMethod.PUT, "/automoveis/**", "/pedidos/**").hasRole("AGENTE")
                .requestMatchers(HttpMethod.DELETE, "/automoveis/**", "/clientes/**").hasRole("AGENTE")
                .requestMatchers("/contratos/**", "/agentes/**").hasRole("AGENTE")
                .anyRequest().authenticated())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }
}
