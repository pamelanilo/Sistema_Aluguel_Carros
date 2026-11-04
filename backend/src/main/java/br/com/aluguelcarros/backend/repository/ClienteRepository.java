package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    java.util.Optional<Cliente> findByLogin(String login);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Long id);
    boolean existsByLogin(String login);
    boolean existsByLoginAndIdNot(String login, Long id);
}